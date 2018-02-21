package settings.build.cli

import constants.ToolConstants
import settings.Settings

abstract class CLISettings extends Settings {
    private CLIType _cliType

    CLISettings(def steps,
                CLIType cliType,
                Map parameters) {
        super(steps)
        _cliType = cliType
        cliParameters = new CLIParameters(parameters)
    }

    CLIParameters cliParameters

    @Override
    protected void init() {
        setTool()
        setFields()
        cliParameters.args = getArgs()
    }

    abstract void setFields()

    abstract String getArgs()

    void run() {
        try {
            /*
            _steps.bat "attrib -r ${_steps.env.WORKSPACE}\\*.* /s"
            _steps.bat "${cliParameters.tool} ${cliParameters.args}"
            */
            _steps.echo "attrib -r ${_steps.env.WORKSPACE}\\*.* /s"
            _steps.echo "${cliParameters.tool} ${cliParameters.args}"
        } catch (error) {
            throw error
        }
    }

    private void setTool() {
        switch (_cliType) {
            case CLIType.MSBUILD:
                cliParameters.tool = ToolConstants.MSBUILD
                break
        }
    }
}
