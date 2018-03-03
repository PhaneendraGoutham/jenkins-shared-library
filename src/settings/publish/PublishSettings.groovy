package settings.publish

import settings.Settings
import settings.publish.types.PublishCollections
import settings.publish.types.PublishFilesets
import settings.publish.types.PublishNodejs
import settings.publish.types.PublishWebServices
import steps.httprequest.HttpRequest
import steps.httprequest.HttpRequestContentType
import steps.httprequest.HttpRequestResponseHandle

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

            /*
            new HttpRequest(
                _steps,
                id
            ).put(
                HttpRequestContentType.APPLICATION_ZIP,
                HttpRequestResponseHandle.NONE,
                publishItem.artifactUrl
            )
            */

            _steps.nexusArtifactUploader artifacts: [
                [
                    artifactId: '',
                    classifier: '',
                    file      : publishItem.zipFile,
                    type      : 'zip'
                ]
            ],
                credentialsId: id,
                groupId: "/${_steps.pipelineSettings.gitSettings.repository}/${_steps.pipelineSettings.gitSettings.version}/${_steps.pipelineSettings.gitSettings.commit}",
                nexusUrl: url,
                nexusVersion: 'nexus3',
                protocol: 'http',
                repository: 'raw-private-sdlc',
                version: _steps.pipelineSettings.gitSettings.version
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
