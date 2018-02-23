package settings.publish.types

import org.apache.commons.io.FilenameUtils
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
    protected String zipFile

    @Override
    protected void init() {
        origin = "${_steps.pipelineSettings.workspaceSettings.artifactsWorkspace}\\${publishItem.publishArtifactType}".toLowerCase()
        parsed = parseInclude()
    }

    def parseInclude() {
        return publishItem.include
    }

    void publish() {
        if (!publishItem.isPublish) {
            return
        }

        create()
        bundle()
        zip()
        archive()
    }

    abstract void bundle()

    void zip() {
        String pathname = "${_steps.pipelineSettings.workspaceSettings.artifactsWorkspace}\\zip"
        File zipDirectory = new File("${pathname}")
        zipDirectory.mkdirs()
        zipFile = "${zipDirectory.getAbsolutePath()}\\${publishItem.name}.${_steps.pipelineSettings.gitSettings.version}.zip"

        _steps.zip dir: "${origin}\\${publishItem.name}",
            glob: '*',
            zipFile: zipFile
    }

    void archive() {
        _steps.dir("${_steps.pipelineSettings.workspaceSettings.artifactsWorkspace}\\zip") {
            _steps.archiveArtifacts allowEmptyArchive: false,
                artifacts: FilenameUtils.getName("${zipFile}"),
                caseSensitive: false,
                fingerprint: true
        }
    }
}
