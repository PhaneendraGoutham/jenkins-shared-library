package settings.deploy

class DeployItem implements Serializable {
    DeployItem(DeployEngineType engineType,
               Map info) {
        deployEngineType = engineType
        this.info = info
    }

    DeployEngineType deployEngineType
    Map info = [:]
}
