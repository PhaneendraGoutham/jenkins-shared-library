package settings.publish.types

import settings.Settings
import settings.publish.PublishItem

abstract class PublishType extends Settings {
    PublishType(def steps,
                PublishItem publishItem) {
        super (steps)
        this.publishItem = publishItem
    }

    protected static String origin
    protected PublishItem publishItem
    protected def parsed

    @Override
    protected void init() {
        origin = "${_steps.pipelineSettings.workspaceSettings.artifactsWorkspace}\\${publishItem.publishArtifactType}".toLowerCase()
        parsed = parseInclude()
    }

    def parseInclude() {
        return publishItem.include
    }

    void publish() {
        create()
        bundle()
        archive()
    }

    abstract void bundle()

    protected void archive() {
        _steps.dir(origin) {
            _steps.archiveArtifacts allowEmptyArchive: true,
                artifacts: '*',
                caseSensitive: false,
                fingerprint: true
        }
    }
}
