package settings

abstract class Settings implements Serializable {
    def _steps

    Settings(def steps) {
        _steps = steps
    }

    void create() {
        init()
        log()
    }

    protected abstract void init()

    void log() {
        _steps.echo "== [Field Info] == ${this.class.name}"

        def fields = this.class
            .declaredFields
            .findAll { !it.synthetic && !it.name.startsWith('__') }
            .collectEntries {
            [(it.name): this."$it.name"]
        }

        for (def field in fields) {
            _steps.echo "${field.key}: ${field.value}"
        }

        _steps.echo "=================="
    }
}
