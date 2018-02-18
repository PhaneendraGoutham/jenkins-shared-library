package settings.publish

class PublishItem implements Serializable {
    PublishItem(PublishArtifactType artifactType,
                Map<String, String> item) {
        publishArtifactType = artifactType
        include = item['include']
        name = item['name']
        repository = item['repository']
    }

    PublishArtifactType publishArtifactType
    String include
    String name
    String repository
    boolean isPublish
}
