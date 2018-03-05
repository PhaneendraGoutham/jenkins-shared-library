package settings.publish.types

import org.apache.commons.io.FileUtils
import settings.publish.PublishItem

class PublishCompiled extends PublishType {
    PublishCompiled(def steps, PublishItem publishItem) {
        super(steps, publishItem)
    }

    @Override
    void bundle() {
        File project = new File("${_steps.env.WORKSPACE}", "${publishItem.include}")
        File bin = new File("${project.getParent()}", "bin")
        File out = bin.listFiles().first().absoluteFile

        final File compiledDirectory = new File("${origin}")
        compiledDirectory.mkdirs()

        _steps.echo "Copying out directory [${out.absolutePath}]..."
        FileUtils.copyDirectoryToDirectory(out, compiledDirectory)

        File copied = new File(compiledDirectory.absolutePath, out.getName())
        File rename = new File(compiledDirectory.absolutePath, "${publishItem.name}")
        copied.renameTo(rename)
    }
}
