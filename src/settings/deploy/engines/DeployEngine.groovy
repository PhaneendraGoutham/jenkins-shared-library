package settings.deploy.engines

import settings.Settings
import settings.deploy.DeployItem

abstract class DeployEngine extends Settings {
    DeployEngine(def steps,
                 DeployItem deployItem) {
        super(steps)
        this.deployItem = deployItem
    }

    protected DeployItem deployItem

    abstract void deploy()
}
