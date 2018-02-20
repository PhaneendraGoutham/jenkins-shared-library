package settings.publish.types

import settings.publish.PublishItem

class PublishWebServices extends PublishType {
    PublishWebServices(def steps, PublishItem publishItem) {
        super(steps, publishItem)
    }

    @Override
    void bundle() {

    }
}
