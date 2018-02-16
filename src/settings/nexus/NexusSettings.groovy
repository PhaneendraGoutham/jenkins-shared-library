package settings.nexus

import settings.Settings

class NexusSettings extends Settings {
    private Map _nexus

    NexusSettings(def steps,
                  def nexus) {
        super(steps)
        _nexus = nexus
    }

    Map<String, Map<String, String>> repositories = [:]

    @Override
    protected void init() {
        populate()
    }

    private void populate() {
        for (def repository in _nexus) {
            Map<String, String> values = repository.value
            repositories.put(repository.key, values)
        }
    }
}
