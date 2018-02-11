package settings.test

import constants.ToolConstants

abstract class TestFramework implements Serializable {
    def _steps
    private String _result
    private TestOptions _testOptions
    private TestTool _testTool
    private String _tool

    TestFramework(def steps,
                  TestOptions testOptions,
                  TestTool testTool) {
        _steps = steps
        _testOptions = testOptions
        _testTool = testTool
    }

    void init() {
        switch (_testTool) {
            case TestTool.NUNIT:
                _tool = ToolConstants.NUNIT
                break
            default:
                throw "Tool not defined for [${_testTool}]."
        }
    }

    boolean test() {
        int status = 0
        try {
            status = _steps.bat returnStatus: true, script: "${_tool} ${_testOptions.options}"
        } catch (error) {
            _steps.echo "${error}"
        } finally {
            archive(status)
            return status
        }
    }

    private void archive(String status) {
        _steps.echo "${_testTool} returned with status of [${status}]."
        _steps.dir("${_steps.pipelineSettings.workspaceSettings.artifactsWorkspace}") {
            switch (_testTool) {
                case TestTool.JUNIT:
                    _steps.junit allowEmptyResults: true,
                        healthScaleFactor: 1.0,
                        keepLongStdio: true,
                        testResults: "**/${_result}"
                    break
                case TestTool.NUNIT:
                    _steps.nunit debug: false,
                        failIfNoResults: false,
                        keepJUnitReports: true,
                        skipJUnitArchiver: false,
                        testResultsPattern: "**/${_result}"
                    break
                default:
                    throw "Tool not defined for [${_testTool}]."
            }
        }
    }
}
