package stages.build

abstract class Build implements Serializable {
    def _steps

    Build(def steps) {
        _steps = steps
    }

    protected String tool
    protected String args

    protected abstract def initialize()

    def compile(String item) {
        try {
            _steps.bat "attrib -r ${_steps.env.WORKSPACE}\\*.* /s"
            _steps.bat "${tool} ${item} ${args}"
        } catch (error) {
            throw error
        }
    }
}
