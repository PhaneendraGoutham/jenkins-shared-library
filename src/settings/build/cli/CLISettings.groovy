package settings.build.cli

import settings.Settings

abstract class CLISettings extends Settings {
    CLISettings(def steps,
                CLIType cliType,
                Map parameters) {
        super(steps)
        this.cliType = cliType
        this.parameters = parameters
    }

    CLIType cliType

    Map parameters = [:]

    String tool = ''

    String args = ''

    @Override
    protected void init() {
        setTool()
        setArgs()
    }

    abstract String setTool()

    abstract String setArgs()

    void issue() {
        try {
            /*
            _steps.bat "attrib -r ${_steps.env.WORKSPACE}\\*.* /s"
            _steps.bat "${tool} ${args}"
            */
            _steps.echo "attrib -r ${_steps.env.WORKSPACE}\\*.* /s"
            _steps.echo "${tool} ${args}"
        } catch (error) {
            throw error
        }
    }
}
