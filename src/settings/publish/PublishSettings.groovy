package settings.publish

import settings.Settings
import settings.publish.types.PublishFilesets

class PublishSettings extends Settings {
    private Map _publish
    private Map<PublishArtifactType, Boolean> _publishParams

    private List<PublishItem> _publishItems = []

    PublishSettings(def steps,
                    def publish,
                    Map<PublishArtifactType, Boolean> publishParams) {
        super(steps)
        _publish = publish
        _publishParams = publishParams
    }

    @Override
    protected void init() {
        populate()
    }

    void publish() {
        for(PublishItem publishItem in _publishItems) {
            switch(publishItem.publishArtifactType) {
                case PublishArtifactType.FILESETS:
                    PublishFilesets publishFilesets = new PublishFilesets(
                        _steps,
                        publishItem
                    )
                    publishFilesets.publish()
                    break
                case PublishArtifactType.WEBSERVICES:
                    break
            }
        }
    }

    private void populate() {
        for (def publishEntry in _publish) {
            String entry = "${publishEntry.key}".toUpperCase()
            PublishArtifactType publishArtifactType = "${entry}" as PublishArtifactType
            PublishItem publishItem = new PublishItem(
                publishArtifactType,
                publishEntry.value
            )

            publishItem.isPublish = _publishParams[publishArtifactType]
            _publishItems.add(publishItem)
        }
    }
}
