package stages.build

abstract class Build implements Serializable {
    def steps
    String tool
    String args

    Build(def steps) {
        this.steps = steps
    }

    protected abstract def initialize()

    def compile(String item) {
        try {
            this.steps.bat "attrib -r ${this.steps.env.WORKSPACE}\\*.* /s"
            this.steps.bat "${this.tool} ${item} ${this.args}"
        } catch (error) {
            throw error
        }
    }
}
