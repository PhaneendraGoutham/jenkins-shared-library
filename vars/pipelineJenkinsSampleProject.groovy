import constants.PipelineConstants
import post.always.Notify
import settings.build.BuildSettings
import settings.git.GitSettings
import settings.nexus.NexusSettings
import settings.nuget.NuGetSettings
import settings.publish.PublishSettings
import settings.test.TestSettings
import settings.vcs.VcsSettings
import settings.workspace.WorkspaceSettings

/**
 * The call(body) method in any file in ~git/vars is exposed as a method with the same name as the file.
 * @param body
 * @return
 */
def call(body) {
    def jenkinsfile = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = jenkinsfile
    body()

    pipelineSettings.workspaceSettings = new WorkspaceSettings(
        this,
        "${jenkinsfile.agent.node.workspace.drive}",
        "${jenkinsfile.agent.node.workspace.root}",
        "${jenkinsfile.agent.node.workspace.leaf}"
    )
    pipelineSettings.workspaceSettings.create()

    pipeline {
        agent {
            node {
                label "${jenkinsfile.agent.node.label}"
                customWorkspace "${pipelineSettings.workspaceSettings.customWorkspace}"
            }
        }

        environment {
            VCS_LOGIN = credentials("${jenkinsfile.vcs.id}")
        }

        options {
            buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '5', daysToKeepStr: '', numToKeepStr: '5'))
            disableConcurrentBuilds()
            timestamps()
        }

        parameters {
            /*
            choice(choices: config.sourceControlManagement.stash.compareRestApiUri
                ? "\n" + setRecentCommits("${BRANCH_NAME}", "${jenkinsfile.vcs.stash.compareRestApiUri}")
                : "",
                description: 'Recent Commit SHA1 Hashes (default is latest; selection builds that version)',
                name: 'commitSha1'
            )
            */
            choice(choices: "${jenkinsfile.parameters.configuration}",
                description: 'Project/Solution build configuration (e.g. Debug, Release).',
                name: 'configuration'
            )
            choice(choices: "${jenkinsfile.parameters.platform}",
                description: 'Project/Solution build platform (e.g. Any CPU, x86).',
                name: 'platform'
            )
            booleanParam(defaultValue: jenkinsfile.parameters.nupkg,
                description: 'Publish nupkg defined in Jenkinsfile (build.nuget.projects).',
                name: 'nupkg'
            )
            booleanParam(defaultValue: jenkinsfile.parameters.filesets,
                description: 'Publish filesets defined in Jenkinsfile (build.artifacts.publish.filesets).',
                name: 'filesets'
            )
            booleanParam(defaultValue: jenkinsfile.parameters.webservices,
                description: 'Publish webservices defined in Jenkinsfile (build.artifacts.publish.webservices).',
                name: 'webservices'
            )
        }

        triggers {
            cron(BRANCH_NAME == "${jenkinsfile.triggers.cron.branch}" ? "${jenkinsfile.triggers.cron.schedule}" : "")
        }

        stages {
            stage('init') {
                steps {
                    script {
                        pipelineSettings.gitSettings = new GitSettings(
                            this,
                            !jenkinsfile.semver
                                ? ''
                                : "${jenkinsfile.semver.gitVersion}"
                        )
                        pipelineSettings.gitSettings.create()

                        pipelineSettings.nexusSettings = new NexusSettings(
                            this,
                            jenkinsfile.nexus
                        )
                        pipelineSettings.nexusSettings.create()

                        pipelineSettings.nuGetSettings = new NuGetSettings(
                            this,
                            jenkinsfile.build.artifacts.nuget
                        )
                        pipelineSettings.nuGetSettings.create()
                    }
                }
                post {
                    failure {
                        script {
                            currentBuild.result = PipelineConstants.FAILURE
                        }
                    }
                    success {
                        script {
                            currentBuild.result = PipelineConstants.SUCCESS
                        }
                    }
                }
            }

            stage('notify(2)') {
                when {
                    expression {
                        return currentBuild.result == PipelineConstants.SUCCESS
                    }
                }
                steps {
                    script {
                        pipelineSettings.vcsSettings = new VcsSettings(
                            this,
                            "${jenkinsfile.vcs.id}",
                            "${jenkinsfile.vcs.svc}",
                            "${jenkinsfile.vcs.scheme}",
                            "${jenkinsfile.vcs.host}",
                            "${jenkinsfile.vcs.project}",
                            "${pipelineSettings.gitSettings.repository}"
                        )
                        pipelineSettings.vcsSettings.create()
                        pipelineSettings.vcsSettings.notify(2)
                    }
                }
                post {
                    failure {
                        script {
                            currentBuild.result = PipelineConstants.FAILURE
                        }
                    }
                    success {
                        script {
                            currentBuild.result = PipelineConstants.SUCCESS
                        }
                    }
                }
            }

            stage('build') {
                when {
                    expression {
                        return currentBuild.result == PipelineConstants.SUCCESS
                    }
                }
                steps {
                    script {
                        pipelineSettings.buildSettings = new BuildSettings(
                            this,
                            jenkinsfile.build.projects
                        )
                        pipelineSettings.buildSettings.create()
                        pipelineSettings.buildSettings.build()
                    }
                }
                post {
                    failure {
                        script {
                            currentBuild.result = PipelineConstants.FAILURE
                        }
                    }
                    success {
                        script {
                            currentBuild.result = PipelineConstants.SUCCESS
                        }
                    }
                }
            }

            stage('test') {
                when {
                    expression {
                        return currentBuild.result == PipelineConstants.SUCCESS &&
                            jenkinsfile.test
                    }
                }
                environment {
                    TEST_RESULT = false
                }
                steps {
                    script {
                        pipelineSettings.testSettings = new TestSettings(
                            this,
                            jenkinsfile.test
                        )
                        pipelineSettings.testSettings.create()
                        TEST_RESULT = pipelineSettings.testSettings.test()
                    }
                }
                post {
                    always {
                        script {
                            currentBuild.result = TEST_RESULT ? PipelineConstants.SUCCESS : PipelineConstants.FAILURE
                        }
                    }
                }
            }

            stage('nupkg') {
                when {
                    expression {
                        return currentBuild.result == PipelineConstants.SUCCESS &&
                            params.nupkg &&
                            jenkinsfile.build.artifacts.nuget
                    }
                }
                steps {
                    script {
                        pipelineSettings.nuGetSettings.pack()
                    }
                }
                post {
                    failure {
                        script {
                            currentBuild.result = PipelineConstants.UNSTABLE
                        }
                    }
                    success {
                        script {
                            pipelineSettings.nuGetSettings.push()
                        }
                    }
                }
            }

            stage('publish') {
                when {
                    expression {
                        return currentBuild.result == PipelineConstants.SUCCESS &&
                            BRANCH_NAME ==~ "${jenkinsfile.directives.when.publish.branch}" &&
                            (params.filesets || params.webservices)
                    }
                }
                steps {
                    script {
                        pipelineSettings.publishSettings = new PublishSettings(
                            this,
                            jenkinsfile.build.artifacts.publish
                        )
                        pipelineSettings.publishSettings.create()
                    }
                }
                post {
                    failure {
                        script {
                            currentBuild.result = PipelineConstants.FAILURE
                        }
                    }
                }
            }
        }

        post {
            always {
                bat 'set > env.out'
                script {
                    Notify.complete(this, "${jenkinsfile.post.always.notify.root}")
                }
            }
            // changed {
            // }
            success {
                script {
                    pipelineSettings.vcsSettings.notify(0)
                    pipelineSettings.workspaceSettings.clean()
                }
            }
            unstable {
                script {
                    pipelineSettings.vcsSettings.notify(0)
                    pipelineSettings.workspaceSettings.clean()
                }
            }
            failure {
                script {
                    pipelineSettings.vcsSettings.notify(1)
                }
            }
            aborted {
                script {
                    pipelineSettings.vcsSettings.notify(1)
                }
            }
        }
    }
}