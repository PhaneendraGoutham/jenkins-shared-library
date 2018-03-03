package settings.publish

import constants.ToolConstants
import settings.Settings
import settings.publish.types.PublishCollections
import settings.publish.types.PublishFilesets
import settings.publish.types.PublishNodejs
import settings.publish.types.PublishWebServices

class PublishSettings extends Settings {
    private Map _publish
    private Map<PublishArtifactType, Boolean> _publishParams

    PublishSettings(def steps,
                    def publish,
                    Map<PublishArtifactType, Boolean> publishParams) {
        super(steps)
        _publish = publish
        _publishParams = publishParams
    }

    List<PublishItem> publishItems = []

    @Override
    protected void init() {
        populate()
    }

    void publish() {
        for (PublishItem publishItem in publishItems) {
            switch (publishItem.publishArtifactType) {
                case PublishArtifactType.COLLECTIONS:
                    PublishCollections publishCollections = new PublishCollections(
                        _steps,
                        publishItem
                    )
                    publishCollections.publish()
                    break
                case PublishArtifactType.FILESETS:
                    PublishFilesets publishFilesets = new PublishFilesets(
                        _steps,
                        publishItem
                    )
                    publishFilesets.publish()
                    break
                case PublishArtifactType.NODEJS:
                    PublishNodejs publishNodejs = new PublishNodejs(
                        _steps,
                        publishItem
                    )
                    publishNodejs.publish()
                    break
                case PublishArtifactType.WEBSERVICES:
                    PublishWebServices publishWebServices = new PublishWebServices(
                        _steps,
                        publishItem
                    )
                    publishWebServices.publish()
                    break
                default:
                    throw "Publish artifact type [${publishItem.publishArtifactType}] not supported."
                    break
            }
        }
    }

    void push() {
        for (PublishItem publishItem in publishItems) {
            if (!publishItem.isPublish) {
                continue
            }

            String repository = publishItem.repository
            String id = _steps.pipelineSettings.nexusSettings.repositories[repository]['id']
            String url = _steps.pipelineSettings.nexusSettings.repositories[repository]['raw']

            String zipFileName = new File("${publishItem.zipFile}").getName()
            publishItem.artifactUrl = sprintf(
                '%1$s/%2$s/%3$s/%4$s/%5$s',
                [
                    url,
                    _steps.pipelineSettings.gitSettings.repository,
                    _steps.pipelineSettings.gitSettings.version,
                    _steps.pipelineSettings.gitSettings.commit,
                    zipFileName
                ])

            _steps.withCredentials([
                _steps.usernameColonPassword(
                    credentialsId: id,
                    variable: 'nexusUsernameColonPassword')]) {
                String tool = ToolConstants.CURL
                String args = sprintf(
                    '-X PUT -u %1$s -T "%2$s" "%3$s"',
                    [
                        "${_steps.env.nexusUsernameColonPassword}",
                        zipFileName,
                        publishItem.artifactUrl
                    ]
                )
                _steps.bat "${tool} ${args}"
            }
        }
    }

    private void populate() {
        for (def publishEntry in _publish) {
            String entry = "${publishEntry.key}".toUpperCase()
            PublishArtifactType publishArtifactType = "${entry}" as PublishArtifactType
            for (def publishSet in publishEntry.value) {
                PublishItem publishItem = new PublishItem(
                    publishArtifactType,
                    publishSet as Map
                )
                publishItem.isPublish = _publishParams[publishArtifactType]
                publishItems.add(publishItem)
            }
        }
    }
}
