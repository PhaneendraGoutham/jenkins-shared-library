package settings.publish

import settings.Settings

class PublishSettings extends Settings {
    private Map _publish
    private List<PublishItem> _publishItems = []

    PublishSettings(def steps,
                    def publish) {
        super(steps)
        _publish = publish
    }

    @Override
    protected void init() {
        populate()
    }

    private void populate() {
        for (def publishEntry in _publish) {
            String entry = "${publishEntry.key}".toUpperCase()
            PublishType publishType = "${entry}" as PublishType
            _steps.echo "publishType: ${publishType}"
            PublishItem publishItem = new PublishItem(
                publishType,
                publishEntry.value
            )
            _publishItems.add(publishItem)
        }
    }
}
