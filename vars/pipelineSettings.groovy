import settings.build.BuildSettings
import settings.build.CLIBuildSettings
import settings.build.cli.CLISettings
import settings.git.GitSettings
import settings.nexus.NexusSettings
import settings.nuget.NuGetSettings
import settings.publish.PublishSettings
import settings.test.TestSettings
import settings.vcs.VcsSettings
import settings.workspace.WorkspaceSettings

WorkspaceSettings getWorkspaceSettings() {
    workspaceSettings
}

GitSettings getGitSettings() {
    gitSettings
}

VcsSettings getVcsSettings() {
    vcsSettings
}

NexusSettings getNexusSettings() {
    nexusSettings
}

NuGetSettings getNuGetSettings() {
    nuGetSettings
}

CLIBuildSettings getCliBuildSettings() {
    cliBuildSettings
}

BuildSettings getBuildSettings() {
    buildSettings
}

TestSettings getTestSettings() {
    testSettings
}

PublishSettings getPublishSettings() {
    publishSettings
}