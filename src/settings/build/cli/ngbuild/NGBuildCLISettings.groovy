package settings.build.cli.ngbuild

import constants.ToolConstants
import settings.build.cli.CLISettings
import settings.build.cli.CLIType

class NGBuildCLISettings extends CLISettings {
    NGBuildCLISettings(def steps, CLIType cliType, Map parameters) {
        super(steps, cliType, parameters)
    }

    String directory

    Map<String, String> options = [:]

    @Override
    void setFields() {
        for (def parameter in cliParameters.parameters) {
            String key = "${parameter.key}".toLowerCase()
            switch (key) {
                case NGBuildCLIConstants.DIRECTORY:
                    directory = sprintf(
                        '"%1$s"',
                        [
                            parameter.value
                        ]
                    )
                    break
                case NGBuildCLIConstants.OPTION:
                    def optionMap = parameter.value as Map<String, String>
                    for (def option in optionMap) {
                        String optionKey = "${option.key}".toLowerCase()
                        String name = NGBuildCLIConstants.OPTIONS[optionKey]
                        String value = "${option.value}"
                        options.put(
                            name,
                            ((value?.trim()) as boolean)
                                ? "${value}"
                                : "${_steps.params[optionKey]}"
                        )
                    }
                    break
            }
        }
    }

    @Override
    String getArgs() {
        String cliArgs = 'build'

        for (def option in options) {
            cliArgs += sprintf(
                ' --%1$s=%2$s',
                [
                    option.key,
                    option.value
                ]
            )
        }

        return cliArgs
    }

    @Override
    void prebuild() {
        try {
            String child = "${directory}".replace('"', '')
            File basedir = new File("${_steps.env.WORKSPACE}", "${child}")
            _steps.dir(basedir.absolutePath) {
                File node_modules = new File("${basedir.absolutePath}", "node_modules")
                _steps.retry(5) {
                    if (node_modules.isDirectory()) {
                        node_modules.deleteDir()
                    }
                    _steps.bat "${ToolConstants.NPM} install"
                }
            }
        } catch (error) {
            throw error
        }
    }

    @Override
    void build() {
        _steps.dir("${directory}".replace('"', '')) {
            try {
                _steps.bat "attrib -r ${_steps.env.WORKSPACE}\\*.* /s"
                _steps.bat "${cliParameters.tool} ${cliParameters.args}"
            } catch (error) {
                throw error
            }
        }
    }
}
