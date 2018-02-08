package settings

abstract class Settings implements Serializable {
    def steps

    Settings(def steps) {
        this.steps = steps
    }

    void create() {
        init()
        log()
    }

    protected abstract void init()

    void log() {
        def classObj = this.getClass()

        steps.echo "==== START: ${classObj.name}"
        for (def property in classObj.declaredFields) {
            steps.echo "${property.name}:"
        }

        steps.echo "==== FINISH: ${classObj.name}"
    }
}
