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

    private void log() {
        def classObj = this.getClass()

        steps.echo "==== ${classObj.class}"
        for (def property in classObj.getProperties()) {
            steps.echo "${property.key}: ${property.value}"
        }

        steps.echo "==== ${classObj.class}"
    }
}
