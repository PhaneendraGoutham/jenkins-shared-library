package settings.publish

class PublishItem implements Serializable {
    PublishItem(PublishArtifactType artifactType,
                def item) {
        publishArtifactType = artifactType
        include = item['include'].toString()
        name = item['name'].toString()
        repository = item['repository'].toString()
    }

    PublishArtifactType publishArtifactType
    String include
    String name
    String repository
    boolean isPublish
}
