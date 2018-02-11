package settings.build

import settings.Settings
import stages.build.MSBuild

class BuildSettings extends Settings {
    private Map _projects

    BuildSettings(def steps,
                  def projects) {
        super(steps)
        _projects = projects
    }

    List<BuildItem> buildItems = []

    @Override
    protected void init() {
        populate()
    }

    void build() {
        MSBuild msBuild = new MSBuild(_steps)
        for (BuildItem buildItem in buildItems) {
            _steps.echo "Executing build workflow for item: [${buildItem.name}]."
            _steps.pipelineSettings.nuGetSettings.restore("${buildItem.path}")
            msBuild.setSwitchValues("${buildItem.configuration}", "${buildItem.platform}")
            msBuild.compile("${buildItem.path}")
        }
    }

    private void populate() {
        for (def project in _projects) {
            BuildItem buildItem = new BuildItem()
            buildItem.name = "${project.key}"
            buildItem.path = "${project.value['path']}"
            buildItem.configuration = "${project.value['configuration']}"?.trim() ?: "${_steps.params.configuration}"
            buildItem.platform = "${project.value['platform']}" ?: "${_steps.params.platform}"
            buildItems.add(buildItem)
        }
    }
}
