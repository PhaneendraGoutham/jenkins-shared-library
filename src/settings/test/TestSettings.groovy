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
            TestFramework testFramework = new TestFramework(
                this,
                "${test.key}",
                test.value
            )
            testFramework.init()
        }
    }
}
