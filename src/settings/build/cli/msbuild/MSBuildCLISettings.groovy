package settings.build.cli.msbuild

import settings.build.cli.CLISettings
import settings.build.cli.CLIType

class MSBuildCLISettings extends CLISettings {
    MSBuildCLISettings(def steps, CLIType cliType, Map parameters) {
        super(steps, cliType, parameters)
    }

    String file = ''

    Map<String, String> properties = [:]

    String verbosity = ''

    @Override
    void setFields() {
        for (def parameter in cliParameters.parameters) {
            String key = "${parameter.key}".toLowerCase()
            switch (key) {
                case MSBuildCLIConstants.FILE:
                    file = sprintf(
                        '"%1$s"',
                        [
                            parameter.value
                        ]
                    )
                    break
                case MSBuildCLIConstants.PROPERTY:
                    def propertyMap = parameter.value as Map<String, String>
                    for (def property in propertyMap) {
                        String name = "${property.key}".toLowerCase()
                        String value = "${property.value}"
                        properties.put(
                            name,
                            ((value?.trim()) as boolean)
                                ? "${value}"
                                : "${_steps.params[name]}"
                        )
                    }
                    break
                case MSBuildCLIConstants.VERBOSITY:
                    switch ("${parameter.value}".toLowerCase()) {
                        case ['quiet', 'minimal', 'normal', 'detailed', 'diagnositc']:
                            verbosity = parameter.value
                            break
                        default:
                            verbosity = 'quiet'
                            break
                    }
                    break
            }
        }
    }

    @Override
    String getArgs() {
        String cliArgs = ''

        cliArgs += file

        for (def property in properties) {
            cliArgs += sprintf(
                ' /property:%1$s="%2$s"',
                [
                    property.key,
                    property.value
                ]
            )
        }

        cliArgs += ' /verbosity:' + verbosity

        return cliArgs
    }
}
