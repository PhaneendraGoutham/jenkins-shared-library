package settings.build

import settings.Settings
import settings.build.cli.CLISettings
import settings.build.cli.CLIType
import settings.build.cli.msbuild.MSBuildCLISettings

class CLIBuildSettings extends Settings {
    private Map _projects

    CLIBuildSettings(def steps,
                     def projects) {
        super(steps)
        _projects = projects
    }

    List<CLISettings> cliBuilds = []

    @Override
    protected void init() {
        populate()
    }

    void build() {
        for (CLISettings cliBuild in cliBuilds) {
            cliBuild.issue()
        }
    }

    private void populate() {
        for (def project in _projects) {
            CLIType cliType = "${project['cli']}".toUpperCase() as CLIType
            switch (cliType) {
                case CLIType.MSBUILD:
                    MSBuildCLISettings msBuildCLISettings = new MSBuildCLISettings(
                        _steps,
                        CLIType.MSBUILD,
                        project['parameters']
                    )
                    msBuildCLISettings.create()
                    cliBuilds.add(msBuildCLISettings)
                    break
            }
        }
    }
}
