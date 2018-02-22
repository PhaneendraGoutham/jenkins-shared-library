package settings.build

import settings.Settings
import settings.build.cli.CLISettings
import settings.build.cli.CLIType
import settings.build.cli.msbuild.MSBuildCLISettings

class BuildSettings extends Settings {
    private List<Map> _projects

    BuildSettings(def steps,
                  def projects) {
        super(steps)
        _projects = projects
    }

    List<CLISettings> cliSettings = []

    @Override
    protected void init() {
        populate()
    }

    void build() {
        for (def cliSetting in cliSettings) {
            if (cliSetting instanceof MSBuildCLISettings) {
                MSBuildCLISettings msBuildCLISettings = cliSetting
                _steps.pipelineSettings.nuGetSettings.restore("${msBuildCLISettings.file}")
            }

            cliSetting.run()
        }
    }

    private void populate() {
        for (def project in _projects) {
            String cli = "${project.get(BuildConstants.CLI)}".toUpperCase()
            CLIType cliType = "${cli}" as CLIType
            def parameters = project.get(BuildConstants.PARAMETERS)
            switch (cliType) {
                case CLIType.MSBUILD:
                    MSBuildCLISettings msBuildCLISettings = new MSBuildCLISettings(
                        _steps,
                        cliType,
                        parameters
                    )
                    msBuildCLISettings.create()
                    cliSettings.add(msBuildCLISettings)
                    break
            }
        }
    }
}
