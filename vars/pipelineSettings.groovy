import settings.build.BuildSettings
import settings.downstream.DownstreamSettings
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

BuildSettings getBuildSettings() {
    buildSettings
}

TestSettings getTestSettings() {
    testSettings
}

PublishSettings getPublishSettings() {
    publishSettings
}

DownstreamSettings getDownstreamSettings() {
    downstreamSettings
}