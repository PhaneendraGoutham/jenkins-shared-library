package settings.test

import settings.Settings

class TestSettings extends Settings {
    private Map _tests
    private List<TestFramework> _testFrameworks = []

    TestSettings(def steps,
                 def tests) {
        super(steps)
        _tests = tests
    }

    Map testResults = [:]

    @Override
    protected void init() {
        populate()
    }

    boolean test() {
        for (TestFramework testFramework in _testFrameworks) {
            testFramework.test()
            testResults.put(testFramework.name, testFramework.result)
        }

        for (boolean result in testResults.values()) {
            if (!result) {
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
            _testFrameworks.add(testFramework)
            testResults.put(testTool, false)
        }
    }
}
