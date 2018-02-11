package settings.test

import settings.Settings

class TestSettings extends Settings {
    private Map _tests

    TestSettings(def steps,
                 def tests) {
        super(steps)
        _tests = tests
    }

    private List<TestFramework> testFrameworks = []

    @Override
    protected void init() {
        populate()
    }

    private void populate() {
        for (def test in _tests) {
            String testTool = "${test.key}".toUpperCase()
            //TestTool _vcsService = "${testTool}" as TestTool
            TestFramework testFramework = new TestFramework(
                _steps,
                "${testTool}" as TestTool,
                test.value
            )
            testFramework.init()
            testFrameworks.add(testFramework)
        }
    }
}
