package settings.git

import constants.PipelineConstants
import constants.ToolConstants
import settings.Settings

class GitSettings extends Settings {
    private String _assembly

    GitSettings(def steps,
                String assembly = '') {
        super(steps)
        _assembly = assembly
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
        branch = "${_steps.BRANCH_NAME}".contains('/') ? "${_steps.BRANCH_NAME}".split('/')[0] : "${_steps.BRANCH_NAME}"
        println "branch: ${branch}"
    }

    void setCommit() {
        String tool = ToolConstants.GIT
        String args = 'rev-parse HEAD'

        def stdout = _steps.bat(returnStdout: true, script: "${tool} ${args}").trim().split("\\n")
        commit = stdout.last()
    }

    void setRepository() {
        url = _steps.scm.getUserRemoteConfigs()[0].getUrl()
        repository = url.split('/').last().replaceAll('.git', '')
    }

    void setVersion() {
        try {
            def tool = ToolConstants.GIT
            def args = 'fetch --all'
            _steps.bat "${tool} ${args}"
        } catch (error) {
            _steps.currentBuild.result = PipelineConstants.FAILURE
            throw error
        }

        def output = "gitversion.json"
        def tool = ToolConstants.GITVERSION
        def args = sprintf(
            '/output json /verbosity error /updateassemblyinfo %1$s > %2$s',
            [
                _assembly,
                output
            ]
        )

        try {
            _steps.bat "${tool} ${args}"
            gitVersion = _steps.readJSON file: "${output}"
            version = "${_steps.BRANCH_NAME}" == 'master' ? gitVersion.MajorMinorPatch : gitVersion.SemVer
            _steps.currentBuild.displayName = version
        }
        catch (error) {
            _steps.currentBuild.result = PipelineConstants.FAILURE
            throw error
        }
    }
}
