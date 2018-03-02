package settings.publish.types

import constants.ToolConstants
import settings.build.cli.CLIType
import settings.build.cli.msbuild.MSBuildCLIConstants
import settings.build.cli.msbuild.MSBuildCLISettings
import settings.publish.PublishItem

class PublishNodejs extends PublishType {
    PublishNodejs(def steps, PublishItem publishItem) {
        super(steps, publishItem)
    }

    @Override
    void bundle() {
        final File outputPath = new File("${origin}\\${publishItem.name}")
        outputPath.mkdirs()

        Map<String, String> parameters = [
            file     : publishItem.include,
            target   : 'package',
            property : [
                configuration   : "${_steps.params[MSBuildCLIConstants.CONFIGURATION]}",
                platform        : "${_steps.params[MSBuildCLIConstants.PLATFORM]}",
                deployiisapppath: "${publishItem.extra['iissite']}\\${publishItem.extra['virtualpath']}",
                outputpath      : "${outputPath.getAbsolutePath()}"
            ],
            verbosity: 'quiet',
            extra: publishItem.extra
        ]

        _steps.retry(5) {
            try {
                File publishproj = new File("${_steps.env.WORKSPACE}\\${publishItem.include}")
                String parent = publishproj.getParent()
                _steps.dir(parent) {
                    File node_modules = new File("${parent}\\node_modules")
                    if (node_modules.isDirectory()) {
                        node_modules.deleteDir()
                    }
                    _steps.bat "${ToolConstants.NPM} install"
                }
            } catch (error) {
                throw error
            }
        }

        MSBuildCLISettings msBuildCLISettings = new MSBuildCLISettings(
            _steps,
            CLIType.MSBUILD,
            parameters
        )
        msBuildCLISettings.create()
        msBuildCLISettings.run()
    }

    @Override
    void zip() {
        String pathname = "${_steps.pipelineSettings.workspaceSettings.artifactsWorkspace}\\zip"
        File zipDirectory = new File("${pathname}")
        zipDirectory.mkdirs()
        publishItem.zipFile = "${zipDirectory.getAbsolutePath()}\\${publishItem.name}.${_steps.pipelineSettings.gitSettings.version}.zip"

        _steps.zip dir: "${origin}\\${publishItem.name}\\_PublishedWebsites\\${publishItem.name}_Package",
            glob: '*',
            zipFile: publishItem.zipFile
    }
}
