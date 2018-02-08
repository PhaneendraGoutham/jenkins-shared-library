import constants.PipelineConstants
import settings.GitSettings

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

    //region agent.node.workspace
    def _checkoutRoot = "!"
    def _artifactRoot = "\$"
    def _nodeWorkspace = setWorkspace("${jenkinsfile.agent.node.workspace.drive}", "${_checkoutRoot}", "${jenkinsfile.agent.node.workspace.folder}")
    def _nodeWorkspaceBranchName = setWorkspaceBranchName("${BRANCH_NAME}")
    def _nodeCustomWorkspace = sprintf(
        '%1$s\\%2$s',
        [
            "${_nodeWorkspace}",
            "${_nodeWorkspaceBranchName}"
        ])
    def _artifactsDirectory = _nodeCustomWorkspace.replace("${_checkoutRoot}", "${_artifactRoot}")
    //endregion

    pipeline {
        agent {
            node {
                label "${jenkinsfile.agent.node.label}"
                customWorkspace "${_nodeCustomWorkspace}"
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
                description: 'Project/Solution Build Configuration (e.g. Debug, Release)',
                name: 'configuration'
            )
            choice(choices: "${jenkinsfile.parameters.platform}",
                description: 'Project/Solution Build Platform (e.g. Any CPU, x86)',
                name: 'platform'
            )
            booleanParam(defaultValue: jenkinsfile.parameters.publishArtifacts,
                description: 'Creates And Publishes Deployment Packages (defined in Jenkinsfile)',
                name: 'publishArtifacts'
            )
            booleanParam(defaultValue: jenkinsfile.parameters.publishNupkg,
                description: 'Creates And Publishes NuGet Packages (defined in Jenkinsfile)',
                name: 'publishNupkg'
            )
        }

        triggers {
            cron(BRANCH_NAME == "${jenkinsfile.triggers.cron.branch}" ? "${jenkinsfile.triggers.cron.schedule}" : "")
        }

        stages {
            stage('init') {
                steps {
                    script {
                        pipelineSettings.gitSettings = new GitSettings(this, '')
                        pipelineSettings.gitSettings.create()
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
        }

        post {
            always {
                bat 'set > env.out'
            }
        }
    }
}