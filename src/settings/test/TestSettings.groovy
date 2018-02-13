package settings.test

import settings.Settings

class TestSettings extends Settings {
    private Map _tests
    private Map<TestTool, TestFramework> _testFrameworks = [:]
    private List<Boolean> _testFrameworkResults = []

    TestSettings(def steps,
                 def tests) {
        super(steps)
        _tests = tests
    }

    @Override
    protected void init() {
        populate()
    }

    boolean test() {
        for (def testFramework in _testFrameworks.values()) {
            testFramework.test()
            _testFrameworkResults.add(testFramework.result)
        }

        _testFrameworkResults.each { result ->
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
            _testFrameworks.put("${testTool}" as TestTool, testFramework)
        }
    }
}
