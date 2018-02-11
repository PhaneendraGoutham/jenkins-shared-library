package settings.test

import constants.ToolConstants

class TestFramework implements Serializable {
    def _steps
    private TestTool _testTool
    private Map _testOptions

    TestFramework(def steps,
                  TestTool testTool,
                  def testOptions) {
        _steps = steps
        _testTool = testTool
        _testOptions = testOptions
    }

    private String result
    private TestOptions testOptions
    private String tool

    void init() {
        switch (_testTool) {
            case TestTool.NUNIT:
                tool = ToolConstants.NUNIT
                for(def testOption in _testOptions) {
                    _steps.echo "${testOption.key}: ${testOption.value}"
                }
                break
            default:
                throw "Tool not defined for [${_testTool}]."
        }
    }

    boolean test() {
        int status = 0
        try {
            status = _steps.bat returnStatus: true, script: "${tool} ${_testOptions.options}"
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
                        testResults: "**/${result}"
                    break
                case TestTool.NUNIT:
                    _steps.nunit debug: false,
                        failIfNoResults: false,
                        keepJUnitReports: true,
                        skipJUnitArchiver: false,
                        testResultsPattern: "**/${result}"
                    break
                default:
                    throw "Tool not defined for [${_testTool}]."
            }
        }
    }
}
