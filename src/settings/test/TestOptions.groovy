package settings.test

class TestOptions {
    private Map _options
    private TestTool _testTool

    TestOptions(Map options,
                TestTool testTool) {
        _options = options
        _testTool = testTool
    }

    String options

    void construct() {
        switch(_testTool) {
            case TestTool.NUNIT:
                options = getNUnitOptions()
                break
            default:
                throw "Tool not defined for [${_testTool}]."
        }
    }

    private String getNUnitOptions() {
        String inputFile = new FileNameFinder()
            .getFileNames("${_steps.env.WORKSPACE}", "${params.assembly}", '')
            .find { true }
        def options = sprintf(
            '"%1$s" --config="%2$s" --result="%3$s\\%4$s" %5$s %6$s',
            [
                inputFile,
                "${params.configuration}",
                "${params.context.configManager.artifactsNunitDirectory}",
                "${result}",
                "${params.where}" ? "--where=\"${params.where}\"" : "",
                "${params.is32Bit}" ? "--x86" : ""
            ])
        return options
    }
}
