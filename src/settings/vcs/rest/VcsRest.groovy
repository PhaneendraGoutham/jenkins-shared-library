package settings.vcs.rest

@Singleton
class VcsRest {
    VcsService vcsService
    String scheme
    String host
    String project
    String repository

    String getCommitsUri() {
        switch (vcsService) {
            case VcsService.GITHUB:
                return "${scheme}://api.${host}/repos/${project}/${repository}/commits"
            case VcsService.STASH:
                return "${scheme}://${host}/rest/api/1.0/projects/${project}/repos/${repository}/compare/commits"
        }
    }

    String getStatusUri() {
        switch (vcsService) {
            case VcsService.GITHUB:
                return "${scheme}://api.${host}/repos/${project}/${repository}/statuses"
            case VcsService.STASH:
                return "${scheme}://${host}/rest/build-status/1.0/commits"
        }
    }
}
