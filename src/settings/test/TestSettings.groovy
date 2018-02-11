package settings.test

import settings.Settings

class TestSettings extends Settings {
    private Map _tests

    TestSettings(def steps,
                 def tests) {
        super(steps)
        _tests = tests
    }

    private TestFramework testFramework
    private TestOptions testOptions

    @Override
    protected void init() {
        populate()
    }

    private void populate() {
        for (def test in _tests) {
            _steps.echo "${test.key}: ${test.value}"
        }
    }
}
