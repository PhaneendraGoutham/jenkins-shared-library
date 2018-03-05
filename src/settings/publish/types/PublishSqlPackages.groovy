package settings.publish.types

import settings.publish.PublishItem

class PublishSqlPackages extends PublishCompiled {
    PublishSqlPackages(def steps, PublishItem publishItem) {
        super(steps, publishItem)
    }
}
