package settings.publish

class PublishItem implements Serializable {
    PublishItem(PublishType type,
                def item) {
        publishType = type
        include = item['include']
        name = item['name']
        repository = item['repository']
    }

    PublishType publishType
    String include
    String name
    String repository
}
