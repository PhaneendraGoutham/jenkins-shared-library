package stages.build

import constants.ToolConstants

class MSBuild extends Build {
    String configuration
    String platform

    MSBuild(Object steps,
            String configuration,
            String platform) {
        super(steps)
        this.configuration = configuration
        this.platform = platform
    }

    @Override
    protected initialize() {
        this.tool = ToolConstants.MSBUILD
        this.args = sprintf(
            '/p:configuration="%1$s" /p:platform="%2$s" /v:minimal',
            [
                "${this.configuration}",
                "${this.platform}"
            ]
        )
    }
}
