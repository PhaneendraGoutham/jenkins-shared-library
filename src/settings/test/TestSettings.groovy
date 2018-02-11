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

    void test() {
        for (def testFramework in testFrameworks) {
            testFramework.test()
        }
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
            testFrameworks.add(testFramework)
        }
    }
}
