package settings.test

import constants.ToolConstants

class TestFramework implements Serializable {
    def _steps
    private TestTool _testTool
    private Map _testOptions

    private String _basedir
    private String _tool
    private String _origin
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
        _origin = "${_steps.pipelineSettings.workspaceSettings.artifactsWorkspace}"
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
        _steps.dir(_origin) {
            switch (_testTool) {
                case TestTool.JUNIT:
                    _steps.junit allowEmptyResults: true,
                        healthScaleFactor: 1.0,
                        keepLongStdio: true,
                        testResults: "${_result}"
                    break
                case TestTool.NUNIT:
                    _steps.nunit debug: false,
                        failIfNoResults: false,
                        keepJUnitReports: true,
                        skipJUnitArchiver: false,
                        testResultsPattern: "/nunit/*.xml"
                    result = false
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

            if (option == NUnitConstants.ASSEMBLY) {
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

            if (option == NUnitConstants.CONFIG) {
                _options += sprintf(
                    ' --%1$s="%2$s"',
                    [
                        NUnitConstants.CONFIG,
                        "${value}"
                    ]
                )
                continue
            }

            if (option == NUnitConstants.RESULT) {
                String pathname = new File("${_origin}", "${_testTool}").absolutePath.toLowerCase()
                File resultDirectory = new File("${pathname}")
                resultDirectory.mkdirs()
                _result = "${resultDirectory.getAbsolutePath()}\\${value}"
                _options += sprintf(
                    ' --%1$s="%2$s"',
                    [
                        NUnitConstants.RESULT,
                        "${_result}"
                    ]
                )
                continue
            }

            if (option == NUnitConstants.WHERE) {
                _options += sprintf(
                    ' --%1$s=\"%2$s\"',
                    [
                        NUnitConstants.WHERE,
                        "${value}"
                    ]
                )
                continue
            }

            if (option == NUnitConstants.IS32BIT) {
                if (value.toBoolean()) {
                    _options += ' --x86'
                }
                continue
            }

            _steps.echo "No option defined for [${option}] with value [${value}]."
        }

        return _options
    }
}
