package settings

import constants.PipelineConstants
import constants.ToolConstants

class GitSettings extends Settings {
    private String assembly

    GitSettings(def steps,
                String assembly = '') {
        super(steps)
        this.assembly = assembly
    }

    String branch
    String commit
    String repository
    String url
    String version
    Properties gitVersion

    @Override
    protected void init() {
        setBranch()
        setCommit()
        setRepository()
        setVersion()
    }

    void setBranch() {
        branch = "${steps.BRANCH_NAME}".contains('/') ? "${steps.BRANCH_NAME}".split('/')[0] : "${steps.BRANCH_NAME}"
        println "branch: ${branch}"
    }

    void setCommit() {
        String tool = ToolConstants.GIT
        String args = 'rev-parse HEAD'

        def stdout = steps.bat(returnStdout: true, script: "${tool} ${args}").trim().split("\\n")
        commit = stdout.last()
    }

    void setRepository() {
        url = steps.scm.getUserRemoteConfigs()[0].getUrl()
        repository = url.split('/').last().replaceAll('.git', '')
    }

    void setVersion() {
        def output = "gitversion.json"
        def tool = ToolConstants.GITVERSION
        def args = sprintf(
            '/output json /verbosity error /updateassemblyinfo %1$s > %2$s',
            [
                assembly,
                output
            ])

        try {
            steps.bat "${tool} ${args}"
            gitVersion = steps.readJSON file: "${output}"
            version = "${steps.BRANCH_NAME}" == 'master' ? gitVersion.MajorMinorPatch : gitVersion.SemVer
            steps.currentBuild.displayName = version
        }
        catch (err) {
            steps.currentBuild.result = PipelineConstants.FAILURE
            throw err
        }
    }
}
