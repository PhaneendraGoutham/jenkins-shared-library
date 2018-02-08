package settings

import java.lang.reflect.Field

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
        for (Field field in classObj.declaredFields) {
            steps.echo "${field.name}: ${field.toString()}"
        }

        steps.echo "==== FINISH: ${classObj.name}"
    }
}
