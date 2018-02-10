package stages.build

abstract class Build implements Serializable {
    def _steps
    String _tool

    Build(def steps,
          String tool) {
        _steps = steps
        _tool = tool
    }

    protected String args

    abstract void setSwitchValues(String... values)

    def compile(String item) {
        try {
            _steps.bat "attrib -r ${_steps.env.WORKSPACE}\\*.* /s"
            _steps.bat "${_tool} ${item} ${args}"
        } catch (error) {
            throw error
        }
    }
}
