package settings.publish

class PublishItem implements Serializable {
    PublishItem(PublishArtifactType artifactType,
                Map publishSet) {
        publishArtifactType = artifactType
        include = publishSet['include']
        name = publishSet['name']
        repository = publishSet['repository']
        extra = publishSet.containsKey('extra') ? publishSet['extra'] : [:]
    }

    PublishArtifactType publishArtifactType
    String include
    String name
    String repository
    Map extra = [:]
    boolean isPublish
}
