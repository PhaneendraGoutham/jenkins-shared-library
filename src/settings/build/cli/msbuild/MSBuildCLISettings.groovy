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
            //def value = parameter.value
            switch (key) {
                case 'file':
                    //setFile(value.toString())
                    file = sprintf(
                        '"%1$s"',
                        [
                            parameter.value
                        ]
                    )
                    break
                case 'property':
                    //setProperties(value as Map<String, String>)
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
                case 'verbosity':
                    //setVerbosity(value.toString())
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

        cliArgs += ' /property:'
        for (def property in properties) {
            cliArgs += sprintf(
                '%1$s="%2$s";',
                [
                    property.key,
                    property.value
                ]
            )
        }

        cliArgs += ' /verbosity:' + verbosity

        return cliArgs
    }

    private void setFile(String value) {
        _steps.echo "value: ${value}"
        file = sprintf(
            '"%1$s"',
            [
                value
            ]
        )
    }

    private void setProperties(Map<String, String> properties) {
        for (def property in properties) {
            String name = "${property.key}".toLowerCase()
            String value = "${property.value}"
            this.properties.put(
                name,
                ((value?.trim()) as boolean)
                    ? "${value}"
                    : "${_steps.params[name]}"
            )
        }
    }

    private void setVerbosity(String value) {
        switch ("${value}".toLowerCase()) {
            case ['quiet', 'minimal', 'normal', 'detailed', 'diagnositc']:
                verbosity = value
                break
            default:
                verbosity = 'quiet'
                break
        }
    }
}
