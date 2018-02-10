package stages.build

import constants.ToolConstants

class MSBuild extends Build {
    MSBuild(def steps, String tool = ToolConstants.MSBUILD) {
        super(steps, tool)
    }

    @Override
    void setSwitchValues(String... values) {
        args = sprintf(
            '/p:configuration="%1$s" /p:platform="%2$s" /v:minimal',
            [
                "${values[0]}",
                "${values[1]}"
            ]
        )
    }
}
