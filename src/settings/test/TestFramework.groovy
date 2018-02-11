package settings.test

import constants.ToolConstants

class TestFramework implements Serializable {
    def _steps
    private TestTool _testTool
    private Map _testOptions
    private String _basedir

    TestFramework(def steps,
                  TestTool testTool,
                  def testOptions) {
        _steps = steps
        _testTool = testTool
        _testOptions = testOptions
    }

    private String tool
    private String options = new String()
    private String result

    void init() {
        _basedir = "${_steps.env.WORKSPACE}"
        switch (_testTool) {
            case TestTool.NUNIT:
                tool = ToolConstants.NUNIT
                options = getNUnitOptions()
                break
            default:
                throw "Tool not defined for [${_testTool}]."
        }
    }

    void test() {
        try {
            _steps.bat "${tool} ${options}"
        } catch (error) {
            _steps.echo "${error}"
        } finally {
            archive()
        }
    }

    private void archive() {
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

    private String getNUnitOptions() {
        for (def testOption in _testOptions) {
            String option = "${testOption.key}"
            def value = "${testOption.value}"

            if (option == 'assembly') {
                def assembly = new FileNameFinder()
                    .getFileNames("${_basedir}", "${value}", '')
                    .find { true }
                options += sprintf(
                    '"%1$s"',
                    [
                        assembly
                    ]
                )
                continue
            }

            if (option == 'config') {
                options += sprintf(
                    ' --config="%1$s"',
                    [
                        "${value}"
                    ]
                )
                continue
            }

            if (option == 'result') {
                result = "${_steps.pipelineSettings.workspaceSettings.artifactsWorkspace}\\nunit\\${value}"
                options += sprintf(
                    ' --result="%1$s"',
                    [
                        "${result}"
                    ]
                )
                continue
            }

            if (option == 'where') {
                options += sprintf(
                    ' --where=\"%1$s\"',
                    [
                        "${value}"
                    ]
                )
                continue
            }

            if (option == 'is32Bit') {
                if (value == true) {
                    options += ' --x86'
                }
                continue
            }

            _steps.echo "No option defined for [${option}] with value [${value}]."
        }

        return options
    }
}
