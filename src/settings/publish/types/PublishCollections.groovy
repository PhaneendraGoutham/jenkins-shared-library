package settings.publish.types

import groovy.io.FileType
import org.apache.commons.io.FileUtils
import settings.publish.PublishItem

class PublishCollections extends PublishType {
    PublishCollections(def steps, PublishItem publishItem) {
        super(steps, publishItem)
    }

    @Override
    def parseInclude() {
        return publishItem.include.tokenize(';')
    }

    @Override
    void bundle() {
        for (String collection in parsed) {
            List<String> items = new FileNameFinder()
                .getFileNames("${_steps.env.WORKSPACE}", "${collection}")

            if (!items) {
                _steps.echo "Could not find anything based on collection [${collection}]."
                continue
            }

            final File collectionDirectory = new File("${origin}\\${publishItem.name}")
            collectionDirectory.mkdirs()

            _steps.echo "Copying items from collection [${collection}]..."
            for (String item in items) {
                final File file = new File("${item}")
                if (!file.exists()) {
                    continue
                }

                FileUtils.copyFileToDirectory(file, collectionDirectory)
                _steps.echo "Copied item [${file}] to [${collectionDirectory.getAbsolutePath()}]."
            }
        }
    }
}
