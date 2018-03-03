package settings.deploy

import settings.Settings
import settings.deploy.engines.SaltStack

class DeploySettings extends Settings {
    private Map _deploy

    DeploySettings(def steps,
                   def deploy) {
        super(steps)
        _deploy = deploy
    }

    List<DeployItem> deployItems = []

    @Override
    protected void init() {
        populate()
    }

    void deploy() {
        for (DeployItem deployItem in deployItems) {
            switch (deployItem.deployEngineType) {
                case DeployEngineType.SALTSTACK:
                    SaltStack saltStack = new SaltStack(
                        _steps,
                        deployItem
                    )
                    saltStack.deploy()
                    break
                default:
                    throw "Deploy engine [${deployItem.deployEngineType}] not supported."
                    break
            }
        }
    }

    private void populate() {
        for (def deployEntry in _deploy) {
            String entry = "${deployEntry.key}".toUpperCase()
            DeployEngineType deployEngineType = "${entry}" as DeployEngineType
            DeployItem deployItem = new DeployItem(
                deployEngineType,
                deployEntry.value as Map
            )
            deployItems.add(deployItem)
        }
    }
}
