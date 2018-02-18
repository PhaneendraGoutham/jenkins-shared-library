package settings.publish.types

import org.apache.commons.io.FileUtils
import settings.publish.PublishItem

class PublishFilesets extends PublishType {
    PublishFilesets(def steps, PublishItem publishItem) {
        super(steps, publishItem)
    }

    @Override
    def parseInclude() {
        return publishItem.include.tokenize(';')
    }

    @Override
    void bundle() {
        for (String fileset in parsed) {
            def item = new FileNameFinder()
                .getFileNames("${_steps.env.WORKSPACE}", "${fileset}")
                .find { true }

            if ("${item}"?.trim()) {
                final File file = new File("${item}")
                if (!file.isFile()) {
                    continue
                }

                final File filesetDirectory = new File("${origin}\\${publishItem.name}")
                filesetDirectory.mkdirs()
                FileUtils.copyFileToDirectory(file, filesetDirectory)
                _steps.echo "Copied fileset [${fileset}] item [${item}] to [${filesetDirectory.getAbsolutePath()}]."
            } else {
                _steps.echo "Could not find anything based on fileset [${fileset}]."
            }
        }
    }
}
