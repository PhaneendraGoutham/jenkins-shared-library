package stages.build

import constants.ToolConstants

class MSBuild extends Build {
    String _configuration
    String _platform

    MSBuild(def steps,
            String configuration,
            String platform) {
        super(steps)
        _configuration = configuration
        _platform = platform
    }

    @Override
    protected initialize() {
        tool = ToolConstants.MSBUILD
        args = sprintf(
            '/p:configuration="%1$s" /p:platform="%2$s" /v:minimal',
            [
                "${_configuration}",
                "${_platform}"
            ]
        )
    }
}
