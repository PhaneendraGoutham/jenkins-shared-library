package settings.build.cli

class CLIParameters {
    CLIParameters(CLIType cliType,
                  Map parameters) {
        this.cliType = cliType
        this.parameters = parameters
    }

    CLIType cliType

    Map parameters = [:]

    String tool

    String args
}
