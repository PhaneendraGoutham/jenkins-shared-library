package stages.vcs.services

@Singleton
class VcsRest {
    VcsHost vcsHost
    String scheme
    String host
    String project
    String repository

    String getCommitsUri() {
        switch (this.vcsHost) {
            case VcsHost.GITHUB:
                return "${this.scheme}://api.${this.host}/repos/${this.project}/${this.repository}/commits"
            case VcsHost.STASH:
                return "${this.scheme}://${this.host}/rest/api/1.0/projects/${this.project}/repos/${this.repository}/compare/commits"
        }
    }

    String getStatusUri() {
        switch (this.vcsHost) {
            case VcsHost.GITHUB:
                return "${this.scheme}://api.${this.host}/repos/${this.project}/${this.repository}/statuses"
            case VcsHost.STASH:
                return "${this.scheme}://${this.host}/rest/build-status/1.0/commits"
        }
    }
}
