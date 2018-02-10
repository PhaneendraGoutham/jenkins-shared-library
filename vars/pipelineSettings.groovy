import settings.build.BuildSettings
import settings.git.GitSettings
import settings.nuget.NuGetSettings
import settings.vcs.VcsSettings

GitSettings getGitSettings() {
    gitSettings
}

VcsSettings getVcsSettings() {
    vcsSettings
}

NuGetSettings getNuGetSettings() {
    nuGetSettings
}

BuildSettings getBuildSettings() {
    buildSettings
}
