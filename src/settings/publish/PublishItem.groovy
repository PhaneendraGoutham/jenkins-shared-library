package settings.publish

class PublishItem implements Serializable {
    PublishItem(PublishArtifactType artifactType,
                def publishSet) {
        publishArtifactType = artifactType
        include = publishSet['include']
        name = publishSet['name']
        repository = publishSet['repository']
    }

    PublishArtifactType publishArtifactType
    String include
    String name
    String repository
    boolean isPublish
}
