package settings.publish.types

import settings.build.cli.CLIType
import settings.build.cli.msbuild.MSBuildCLIConstants
import settings.build.cli.msbuild.MSBuildCLISettings
import settings.publish.PublishItem

class PublishWebServices extends PublishType {
    PublishWebServices(def steps, PublishItem publishItem) {
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
            extra    : publishItem.extra
        ]

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
        _steps.zip dir: "${origin}\\${publishItem.name}\\_PublishedWebsites\\${publishItem.name}_Package",
            glob: '*',
            zipFile: publishItem.zipFile.getAbsolutePath()
    }
}
