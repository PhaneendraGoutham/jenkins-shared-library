package settings.build

import settings.Settings
import settings.build.cli.CLISettings
import settings.build.cli.CLIType
import settings.build.cli.msbuild.MSBuildCLISettings

class CLIBuildSettings extends Settings {
    private List<Map> _projects

    CLIBuildSettings(def steps,
                     def projects) {
        super(steps)
        _projects = projects
    }

    List<CLISettings> cliBundle = []

    @Override
    protected void init() {
        populate()
    }

    void build() {
        for (def cliSetting in cliBundle) {
            /*
            switch (cliSetting) {
                case { it instanceof MSBuildCLISettings }:
                    MSBuildCLISettings msBuildCLISettings = cliSetting as MSBuildCLISettings
                    _steps.pipelineSettings.nuGetSettings.restore("${msBuildCLISettings.file}")
                    break
            }
            */

            if (cliSetting instanceof MSBuildCLISettings) {
                _steps.echo "instanceof MSBuildCLISettings"
            }

            cliSetting.run()
        }
    }

    private void populate() {
        for (def project in _projects) {
            String cli = "${project.get('cli')}".toUpperCase()
            CLIType cliType = "${cli}" as CLIType
            def parameters = project.get('parameters')
            switch (cliType) {
                case CLIType.MSBUILD:
                    MSBuildCLISettings msBuildCLISettings = new MSBuildCLISettings(
                        _steps,
                        cliType,
                        parameters
                    )
                    msBuildCLISettings.create()
                    cliBundle.add(msBuildCLISettings)
                    break
            }
        }
    }
}
