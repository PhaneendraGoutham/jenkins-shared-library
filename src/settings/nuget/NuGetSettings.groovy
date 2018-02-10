package settings.nuget

import constants.ToolConstants
import settings.Settings

class NuGetSettings extends Settings {
    private def _sources
    private def _tool

    NuGetSettings(def steps,
                  String sources) {
        super(steps)
        _sources = sources
    }

    @Override
    protected void init() {
        _tool = ToolConstants.NUGET
    }

    void restore(String project) {
        def args = sprintf(
            'restore -source %1$s -noninteractive "%2$s"',
            [
                "${_sources}",
                "${project}"
            ])

        try {
            _steps.bat "${_tool} ${args}"
        }
        catch (err) {
            throw err
        }
    }
}
