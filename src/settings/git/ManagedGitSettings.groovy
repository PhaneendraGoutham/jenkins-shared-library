package settings.git

import constants.PipelineConstants
import settings.Settings

class ManagedGitSettings extends Settings {
    private String _cpp
    private String _h

    ManagedGitSettings(def steps,
                       String cpp,
                       String h) {
        super(steps)
        _cpp = cpp
        _h = h
    }

    @Override
    protected void init() {
        updateCpp()
        updateH()
    }

    private void updateCpp() {
        def script = """
        \$path = \"${_cpp}\"
        \$assemblyVersion = '\\[assembly:AssemblyVersion\\("(.*)"\\)\\];'
        \$assemblyFileVersion = '\\[assembly:AssemblyFileVersion\\("(.*)"\\)\\];'
        \$assemblyInformationalVersion = '\\[assembly:AssemblyInformationalVersion\\("(.*)"\\)\\];'
        (Get-Content \$path) | ForEach-Object {
            if (\$_ -match \$assemblyVersion) {
                \$newVersion = \"{0}\" -f \"${_steps.pipelineSettings.gitSettings.gitVersion.MajorMinorPatch}\"
                '[assembly:AssemblyVersion("{0}")];' -f \$newVersion
            } elseif (\$_ -match \$assemblyFileVersion) {
                \$newVersion = "{0}" -f \"${_steps.pipelineSettings.gitSettings.gitVersion.MajorMinorPatch}\"
                '[assembly:AssemblyFileVersion("{0}")];' -f \$newVersion
            } elseif (\$_ -match \$assemblyInformationalVersion) {
                \$newVersion = "{0}" -f \"${_steps.pipelineSettings.gitSettings.gitVersion.InformationalVersion}\"
                '[assembly:AssemblyInformationalVersion("{0}")];' -f \$newVersion
            } else {
                \$_
            }
        } | Set-Content \$path
    """

        try {
            _steps.powershell "${script}"
        }
        catch (error) {
            _steps.currentBuild.result = PipelineConstants.FAILURE
            throw error
        }
    }

    private void updateH() {
        def script = """
        \$path = \"${_h}\"
        \$majorVersion = '#define MAJOR_VERSION (\\d+)'
        \$minorVersion = '#define MINOR_VERSION (\\d+)'
        \$patchVersion = '#define PATCH_VERSION (\\d+)'
        (Get-Content \$path) | ForEach-Object {
            if (\$_ -match \$majorVersion) {
                '#define MAJOR_VERSION {0}' -f \"${_steps.pipelineSettings.gitSettings.gitVersion.Major}\"
            } elseif (\$_ -match \$minorVersion) {
                '#define MINOR_VERSION {0}' -f \"${_steps.pipelineSettings.gitSettings.gitVersion.Minor}\"
            } elseif (\$_ -match \$patchVersion) {
                '#define PATCH_VERSION {0}' -f \"${_steps.pipelineSettings.gitSettings.gitVersion.Patch}\"
            } else {
                \$_
            }
        } | Set-Content \$path
    """

        try {
            _steps.powershell "${script}"
        }
        catch (error) {
            _steps.currentBuild.result = PipelineConstants.FAILURE
            throw error
        }
    }
}
