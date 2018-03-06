package settings.deploy.engines

import constants.ToolConstants
import org.apache.commons.io.FilenameUtils
import settings.deploy.DeployItem

class SqlPackage extends DeployEngine {
    private String _action
    private String _sourcefile
    private Map<String, String> _profile = [:]
    private Map<String, String> _tdnsuffix = [:]

    SqlPackage(def steps, DeployItem deployItem) {
        super(steps, deployItem)
        _action = deployItem.info['action']
        _sourcefile = deployItem.info['sourcefile']
        _profile = deployItem.info['profile'] as Map
        _tdnsuffix = deployItem.info['tdnsuffix'] as Map
    }

    String sourcefile
    String profile
    String tdn
    String tdnsuffix

    @Override
    void install() {
        setup()
        validate()
        apply()
    }

    private void setup() {
        String branch = _steps.pipelineSettings.gitSettings.branch

        sourcefile = new FileNameFinder()
            .getFileNames("${_steps.env.WORKSPACE}", "${_sourcefile}")
            .find { true }

        String xml = _profile.containsKey(branch) ? _profile[branch] : ''
        profile = new FileNameFinder()
            .getFileNames("${_steps.env.WORKSPACE}", "${xml}")
            .find { true }

        tdn = FilenameUtils.getBaseName(new File("${sourcefile}").getName())
        tdnsuffix = _tdnsuffix.containsKey(branch) ? _tdnsuffix[branch] : _steps.pipelineSettings.workspaceSettings.branchName
    }

    private void validate() {
        if (!"${sourcefile}"?.trim()) {
            throw "Source file was not found using pattern [${_sourcefile}]."
        }

        if (!profile) {
            throw "Profile was not found using branch [${_steps.pipelineSettings.gitSettings.branch}]."
        }
    }

    private void apply() {
        String tool = ToolConstants.SQLPACKAGE
        String args = sprintf(
            '/action:%1$s /sourcefile:"%2$s" /profile:"%3$s" /targetdatabasename:"%4$s"',
            [
                _action,
                sourcefile,
                profile,
                tdn + tdnsuffix
            ]
        )

        try {
            _steps.bat "${tool} ${args}"
        } catch (error) {
            throw error
        }
    }
}
