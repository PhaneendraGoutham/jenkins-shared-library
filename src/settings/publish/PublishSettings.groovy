package settings.publish

import settings.Settings

class PublishSettings extends Settings {
    private Map _publish
    private Map<PublishType, Boolean> _publishParams

    private List<PublishItem> _publishItems = []

    PublishSettings(def steps,
                    def publish,
                    Map<PublishType, Boolean> publishParams) {
        super(steps)
        _publish = publish
        _publishParams = publishParams
    }

    @Override
    protected void init() {
        populate()
    }

    private void populate() {
        for (def publishEntry in _publish) {
            String entry = "${publishEntry.key}".toUpperCase()
            PublishType publishType = "${entry}" as PublishType
            PublishItem publishItem = new PublishItem(
                publishType,
                publishEntry.value
            )

            publishItem.isPublish = _publishParams[publishType]
            _publishItems.add(publishItem)
        }

        for(PublishItem publishItem in _publishItems) {
            println "publishType: ${publishItem.publishType}"
            println "include: ${publishItem.include}"
            println "name: ${publishItem.name}"
            println "repository: ${publishItem.repository}"
            println "isPublish: ${publishItem.isPublish}"
        }
    }
}
