package settings.build.cli

class CLIParameters {
    CLIParameters(Map parameters) {
        this.parameters = parameters
    }

    Map parameters = [:]

    String tool

    String args
}
