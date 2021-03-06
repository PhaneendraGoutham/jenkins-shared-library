package settings.build.cli

import constants.ToolConstants
import settings.Settings

abstract class CLISettings extends Settings {
    CLISettings(def steps,
                CLIType cliType,
                Map parameters) {
        super(steps)
        cliParameters = new CLIParameters(cliType, parameters)
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

    void compile() {
        prebuild()
        build()
        postbuild()
    }

    void prebuild() {}

    void build() {
        try {
            _steps.bat "attrib -r ${_steps.env.WORKSPACE}\\*.* /s"
            _steps.bat "${cliParameters.tool} ${cliParameters.args}"
        } catch (error) {
            throw error
        }
    }

    void postbuild() {}

    void setTool() {
        switch (cliParameters.cliType) {
            case CLIType.MSBUILD:
                cliParameters.tool = ToolConstants.MSBUILD
                break
            case CLIType.NGBUILD:
                cliParameters.tool = ToolConstants.NGBUILD
                break
        }
    }
}
