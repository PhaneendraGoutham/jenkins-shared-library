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
            _steps.echo "publishType: ${publishItem.publishType}"
            _steps.echo "include: ${publishItem.include}"
            _steps.echo "name: ${publishItem.name}"
            _steps.echo "repository: ${publishItem.repository}"
            _steps.echo "isPublish: ${publishItem.isPublish}"
        }
    }
}
