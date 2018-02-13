package settings.test

import settings.Settings

class TestSettings extends Settings {
    private Map _tests
    private Map<TestTool, TestFramework> _testFrameworks = [:]
    //private List<TestFramework> _testFrameworks = []

    TestSettings(def steps,
                 def tests) {
        super(steps)
        _tests = tests
    }

    //Map testResults = [:]
    List<Boolean> results = []

    @Override
    protected void init() {
        populate()
    }

    boolean test() {
        /*
        for (TestFramework testFramework in _testFrameworks) {
            testFramework.test()
            testResults["${testFramework.name}"] = testFramework.result
        }

        for (def testResult in testResults) {
            _steps.echo "test(): testResult -> ${testResult.key}: ${testResult.value}"
        }

        for (boolean result in testResults.values()) {
            _steps.echo "test(): result = [${result}]"
            if (!result) {
                return false
            }
        }
        */

        for (def testFramework in _testFrameworks.values()) {
            testFramework.test()
            results.add(testFramework.result)
        }

        results.each { it ->
            if (!it) {
                return false
            }
        }

        return true
    }

    private void populate() {
        for (def test in _tests) {
            String testTool = "${test.key}".toUpperCase()
            TestFramework testFramework = new TestFramework(
                _steps,
                "${testTool}" as TestTool,
                test.value
            )
            testFramework.init()
            //_testFrameworks.add(testFramework)
            _testFrameworks.put("${testTool}" as TestTool, testFramework)
            //testResults.put(testTool, false)
        }
    }
}
