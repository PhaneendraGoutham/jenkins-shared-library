package settings.publish

import constants.NexusConstants
import settings.Settings
import settings.publish.types.PublishFilesets
import settings.publish.types.PublishWebServices
import steps.httprequest.HttpRequest
import steps.httprequest.HttpRequestContentType
import steps.httprequest.HttpRequestCustomHeaders
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
                case PublishArtifactType.FILESETS:
                    PublishFilesets publishFilesets = new PublishFilesets(
                        _steps,
                        publishItem
                    )
                    publishFilesets.publish()
                    break
                case PublishArtifactType.WEBSERVICES:
                    PublishWebServices publishWebServices = new PublishWebServices(
                        _steps,
                        publishItem
                    )
                    publishWebServices.publish()
                    break
            }
        }
    }

    void push() {
        String id = _steps.pipelineSettings.nexusSettings.repositories[NexusConstants.RAW]['id']
        String url = _steps.pipelineSettings.nexusSettings.repositories[NexusConstants.RAW]['sdlc']

        for (PublishItem publishItem in publishItems) {
            if (!publishItem.isPublish) {
                continue
            }

            String zipFileName = new File("${publishItem.zipFile}").getName()
            String artifact = "${url}/${_steps.pipelineSettings.gitSettings.repository}/${_steps.pipelineSettings.gitSettings.version}/${zipFileName}"

            new HttpRequest(
                _steps,
                id
            ).put(
                HttpRequestContentType.APPLICATION_ZIP,
                HttpRequestResponseHandle.NONE,
                artifact
            )
        }

        _steps.dir(_steps.pipelineSettings.workspaceSettings.artifactsWorkspace) {
            String pathname = "${_steps.pipelineSettings.workspaceSettings.artifactsWorkspace}\\${_steps.pipelineSettings.gitSettings.commit}.txt"
            File commit = new File("${pathname}")
            commit.createNewFile()
            def newline = System.getProperty("line.separator")
            for (def param in _steps.params) {
                commit.append("${param.key}: ${param.value}${newline}")
            }

            new HttpRequest(
                _steps,
                id
            ).put(
                HttpRequestResponseHandle.NONE,
                new HttpRequestCustomHeaders(false, 'Content-type', 'text'),
                "${url}/${_steps.pipelineSettings.gitSettings.repository}/${_steps.pipelineSettings.gitSettings.version}/${commit.getName()}"
            )
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
