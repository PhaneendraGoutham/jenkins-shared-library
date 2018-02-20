package settings.build.cli.msbuild

import constants.ToolConstants
import settings.build.cli.CLISettings
import settings.build.cli.CLIType

class MSBuildCLISettings extends CLISettings {
    MSBuildCLISettings(def steps, CLIType cliType, Map parameters) {
        super(steps, cliType, parameters)
    }

    @Override
    String setTool() {
        tool = ToolConstants.MSBUILD
    }

    @Override
    String setArgs() {
        String file = ''
        String properties = ''
        String verbosity = ''
        for (def parameter in parameters) {
            String key = "${parameter.key}".toLowerCase()
            def value = parameter.value
            switch (key) {
                case 'file':
                    file += getFile("${value}")
                    break
                case 'property':
                    properties += getProperties(value as Map<String, String>)
                    break
                case 'verbosity':
                    verbosity += getVerbosity("${value}")
                    break
            }
        }

        args = "${file} ${properties} ${verbosity}"
    }

    private String getFile(String value) {
        return sprintf(
            '"%1$s"',
            [
                "${value}"
            ]
        )
    }

    private String getProperties(Map<String, String> properties) {
        String arg = '/property:'
        for (def property in properties) {
            String name = "${property.key}"
            String value = "${property.value}"
            switch ("${name}".toLowerCase()) {
                case 'configuration':
                    arg += sprintf(
                        'configuration="%1$s";',
                        [
                            ((value?.trim()) as boolean)
                                ? "${value}"
                                : "${_steps.params.configuration}"
                        ]
                    )
                    break
                case 'platform':
                    arg += sprintf(
                        'platform="%1$s";',
                        [
                            ((value?.trim()) as boolean)
                                ? "${value}"
                                : "${_steps.params.platform}"
                        ]
                    )
                    break
            }
        }
        return arg
    }

    private String getVerbosity(String value) {
        String arg = '/verbosity:'
        switch ("${value}".toLowerCase()) {
            case ['quiet', 'minimal', 'normal', 'detailed', 'diagnositc']:
                arg += value
                break
            default:
                arg += 'quiet'
                break
        }
        return arg
    }
}
