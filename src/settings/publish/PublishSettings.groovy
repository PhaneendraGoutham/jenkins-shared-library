package settings.publish

import constants.GitFlowConstants
import constants.ToolConstants
import settings.Settings
import settings.publish.types.PublishCompiled
import settings.publish.types.PublishFilesets
import settings.publish.types.PublishNodejs
import settings.publish.types.PublishSqlPackages
import settings.publish.types.PublishWebServices
import settings.publish.types.PublishWindowsServices

class PublishSettings extends Settings {
    private Map _publish

    PublishSettings(def steps,
                    def publish,
                    Map<PublishArtifactType, Boolean> publishParams) {
        super(steps)
        _publish = publish
        this.publishParams = publishParams
    }

    Map<PublishArtifactType, Boolean> publishParams

    List<PublishItem> publishItems = []

    @Override
    protected void init() {
        populate()
    }

    void publish() {
        for (PublishItem publishItem in publishItems) {
            switch (publishItem.publishArtifactType) {
                case PublishArtifactType.COMPILED:
                    PublishCompiled publishCompiled = new PublishCompiled(
                        _steps,
                        publishItem
                    )
                    publishCompiled.publish()
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
                case PublishArtifactType.SQLPACKAGES:
                    PublishSqlPackages publishSqlPackages = new PublishSqlPackages(
                        _steps,
                        publishItem
                    )
                    publishSqlPackages.publish()
                    break
                case PublishArtifactType.WEBSERVICES:
                    PublishWebServices publishWebServices = new PublishWebServices(
                        _steps,
                        publishItem
                    )
                    publishWebServices.publish()
                    break
                case PublishArtifactType.WINDOWSSERVICES:
                    PublishWindowsServices publishWindowsServices = new PublishWindowsServices(
                        _steps,
                        publishItem
                    )
                    publishWindowsServices.publish()
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
            if (!repository?.trim()) {
                continue
            }

            String branch = _steps.pipelineSettings.gitSettings.branch
            String id = _steps.pipelineSettings.nexusSettings.repositories[repository]['id']
            String url = _steps.pipelineSettings.nexusSettings.repositories[repository][branch]

            String zipFileName = publishItem.zipFile.getName()
            String artifactUrl
            switch (_steps.pipelineSettings.gitSettings.branch) {
                case [GitFlowConstants.DEVELOP, GitFlowConstants.MASTER]:
                    artifactUrl = sprintf(
                        '%1$s/%2$s/%3$s/%4$s/%5$s',
                        [
                            url,
                            _steps.pipelineSettings.gitSettings.repository,
                            _steps.pipelineSettings.gitSettings.version,
                            _steps.pipelineSettings.gitSettings.commit,
                            zipFileName
                        ])
                    break
                default:
                    artifactUrl = sprintf(
                        '%1$s/%2$s/%3$s/%4$s/%5$s/%6$s',
                        [
                            url,
                            _steps.pipelineSettings.gitSettings.repository,
                            _steps.pipelineSettings.workspaceSettings.branch,
                            _steps.pipelineSettings.gitSettings.version,
                            _steps.pipelineSettings.gitSettings.commit,
                            zipFileName
                        ])
                    break
            }
            publishItem.artifactUrl = artifactUrl

            _steps.withCredentials([
                _steps.usernameColonPassword(
                    credentialsId: id,
                    variable: 'nexusUsernameColonPassword')]) {
                String tool = ToolConstants.CURL
                String args = sprintf(
                    '-X PUT -u %1$s -T "%2$s" "%3$s"',
                    [
                        "${_steps.env.nexusUsernameColonPassword}",
                        publishItem.zipFile.getAbsolutePath(),
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
                publishItem.isPublish = publishParams[publishArtifactType]
                publishItems.add(publishItem)
            }
        }
    }
}
