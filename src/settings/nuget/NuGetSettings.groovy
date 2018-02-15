package settings.nuget

import constants.ToolConstants
import settings.Settings

class NuGetSettings extends Settings {
    private Map _nuget
    private def _tool

    NuGetSettings(def steps,
                  def nuget) {
        super(steps)
        _nuget = nuget
    }

    Map<String, Boolean> options = [:]
    List<String> projects = []
    List<String> sources = []

    @Override
    protected void init() {
        _tool = ToolConstants.NUGET
        populate()
    }

    void pack() {
        if (!projects) {
            _steps.echo "No projects to pack."
            return
        }

        _steps.dir("${_steps.pipelineSettings.workspaceSettings.artifactsWorkspace}\\nupkg") {
            for (def project in projects) {
                try {
                    def args = sprintf(
                        'pack %1$s -output %2$s -properties configuration="%3$s";platform="%4$s" -symbols -version %5$s',
                        [
                            "${_steps.env.WORKSPACE}\\${project}".replaceAll("\\[|\\]| ", ""),
                            ".",
                            "${_steps.params.configuration}",
                            "${_steps.params.platform}".replaceAll("\\s", ""),
                            "${_steps.pipelineSettings.gitSettings.version}"
                        ])

                    if (options) {
                        for (def option in options) {
                            if (option.key == NuGetConstants.INCLUDE_REFERENCED_PROJECTS && option.value) {
                                args += sprintf(
                                    ' -%1$s',
                                    [
                                        NuGetConstants.INCLUDE_REFERENCED_PROJECTS
                                    ]
                                )
                                continue
                            }

                            if (option.key == NuGetConstants.TOOL && option.value) {
                                args += sprintf(
                                    ' -%1$s',
                                    [
                                        NuGetConstants.TOOL
                                    ]
                                )
                                continue
                            }

                            _steps.echo "No option defined for [${option.key}] with value [${option.value}]."
                        }
                    }

                    _steps.bat "${_tool} ${args}"
                }
                catch (err) {
                    throw err
                }
            }
        }
    }

    void push() {

    }

    void restore(String project) {
        def args = sprintf(
            'restore -source %1$s -noninteractive "%2$s"',
            [
                sources.join(';'),
                "${project}"
            ]
        )

        try {
            _steps.bat "${_tool} ${args}"
        }
        catch (err) {
            throw err
        }
    }

    private void populate() {
        for (def object in _nuget) {
            switch (object.key) {
                case NuGetConstants.OPTIONS:
                    Map values = object.value
                    for (def option in values) {
                        options.put(option.key.toString(), option.value)
                    }
                    break
                case NuGetConstants.PROJECTS:
                    for (String project in object.value) {
                        projects.add(project)
                    }
                    break
                case NuGetConstants.SOURCES:
                    for (String source in object.value) {
                        sources.add(source)
                    }
                    break
                default:
                    _steps.echo "Object [${object}] is not supported."
                    break
            }
        }
    }
}
