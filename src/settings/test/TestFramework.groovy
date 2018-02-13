package settings.test

import constants.ToolConstants

class TestFramework implements Serializable {
    def _steps
    private TestTool _testTool
    private Map _testOptions

    private String _basedir
    private String _tool
    private String _options = new String()
    private String _result
    private int _status = 0

    TestFramework(def steps,
                  TestTool testTool,
                  def testOptions) {
        _steps = steps
        _testTool = testTool
        _testOptions = testOptions
    }

    boolean result = false

    void init() {
        _basedir = "${_steps.env.WORKSPACE}"
        switch (_testTool) {
            case TestTool.NUNIT:
                _tool = ToolConstants.NUNIT
                _options = getNUnitOptions()
                break
            default:
                throw "Tool not defined for [${_testTool}]."
        }
    }

    void test() {
        try {
            _status = _steps.bat returnStatus: true, script: "${_tool} ${_options}"
        } catch (error) {
            _steps.echo "${error}"
            result = false
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
                        testResults: "${_result}"
                    break
                case TestTool.NUNIT:
                    if (_status == 0) {
                        _steps.nunit debug: false,
                            failIfNoResults: false,
                            keepJUnitReports: true,
                            skipJUnitArchiver: false,
                            testResultsPattern: "/nunit/*xml"
                        result = true
                    } else {
                        _steps.echo "NUnit test status is [${_status}]; will not archive."
                    }
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
                _options += sprintf(
                    '"%1$s"',
                    [
                        assembly
                    ]
                )
                continue
            }

            if (option == 'config') {
                _options += sprintf(
                    ' --config="%1$s"',
                    [
                        "${value}"
                    ]
                )
                continue
            }

            if (option == 'result') {
                String pathname = "${_steps.pipelineSettings.workspaceSettings.artifactsWorkspace}\\${_testTool}".toLowerCase()
                File resultDirectory = new File("${pathname}")
                resultDirectory.mkdirs()
                _result = "${resultDirectory.getAbsolutePath()}\\${value}"
                _options += sprintf(
                    ' --result="%1$s"',
                    [
                        "${_result}"
                    ]
                )
                continue
            }

            if (option == 'where') {
                _options += sprintf(
                    ' --where=\"%1$s\"',
                    [
                        "${value}"
                    ]
                )
                continue
            }

            if (option == 'is32Bit') {
                if (value == true) {
                    _options += ' --x86'
                }
                continue
            }

            _steps.echo "No option defined for [${option}] with value [${value}]."
        }

        return _options
    }
}
