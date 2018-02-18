package settings.publish.types

import settings.Settings
import settings.publish.PublishItem

abstract class PublishType extends Settings {
    private String _zipFile

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
        zip()
        archive()
    }

    abstract void bundle()

    private void zip() {
        String pathname = "${_steps.pipelineSettings.workspaceSettings.artifactsWorkspace}\\zip"
        File zip = new File("${pathname}")
        zip.mkdirs()
        _zipFile = "${zip.getAbsolutePath()}\\${publishItem.name}.${_steps.pipelineSettings.gitSettings.version}.zip"
        _steps.zip dir: "${origin}\\${publishItem.name}",
            glob: '*',
            zipFile: _zipFile
    }

    private void archive() {
        _steps.archiveArtifacts allowEmptyArchive: false,
            artifacts: _zipFile,
            caseSensitive: false,
            fingerprint: true
    }
}
