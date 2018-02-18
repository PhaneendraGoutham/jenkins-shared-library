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
        this.zip()
        this.archive()
    }

    abstract void bundle()

    private void zip() {
        _steps.echo "zip()"
        String pathname = "${_steps.pipelineSettings.workspaceSettings.artifactsWorkspace}\\zip"
        File zipDirectory = new File("${pathname}")
        zipDirectory.mkdirs()
        /*
        _zipFile = "${zipDirectory.getAbsolutePath()}\\${publishItem.name}.${_steps.pipelineSettings.gitSettings.version}.zip"
        _steps.zip dir: "${origin}\\${publishItem.name}",
            glob: '*',
            zipFile: _zipFile
        */
    }

    private void archive() {
        _steps.echo "archive()"
        /*
        _steps.archiveArtifacts allowEmptyArchive: false,
            artifacts: _zipFile,
            caseSensitive: false,
            fingerprint: true
        */
    }
}
