package stages.vcs.services

class Vcs implements Serializable {
    private def steps
    private VcsHost vcsHost
    private String scheme
    private String host
    private String project
    private String repository

    VcsRest vcsRest

    Vcs(def steps,
        VcsHost vcsHost,
        String scheme,
        String host,
        String project,
        String repository) {
        this.steps = steps
        this.vcsHost = vcsHost
        this.scheme = scheme
        this.host = host
        this.project = project
        this.repository = repository
    }

    private void init() {

    }
}
