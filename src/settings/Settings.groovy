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

        def dmap = this.class.declaredFields.findAll { !it.synthetic }.collectEntries {
            [ (it.name):this."$it.name" ]
        }

        for (def entry in dmap) {
            steps.echo "${entry.key}: ${entry.value}"
        }

        steps.echo "==== FINISH: ${classObj.name}"
    }
}
