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
        steps.echo "==== START: ${this.class.name}"

        def fields = this.class
            .declaredFields
            .findAll { !it.synthetic && !it.name.startsWith('__') }
            .collectEntries {
            [(it.name): this."$it.name"]
        }

        for (def field in fields) {
            steps.echo "${field.key}: ${field.value}"
        }

        steps.echo "==== FINISH: ${this.class.name}"
    }
}
