package settings.vcs

import groovy.json.JsonOutput
import settings.Settings
import steps.httprequest.HttpRequest
import steps.httprequest.HttpRequestContentType
import steps.httprequest.HttpRequestResponseHandle

class VcsSettings extends Settings {
    private final String _label = "Status updated by Jenkins Automation Server."

    private String _id
    private String _svc
    private VcsService _vcsService
    private String _scheme
    private String _host
    private String _project
    private String _repository

    private def _requestBody

    VcsSettings(def steps,
                String id,
                String svc,
                String scheme,
                String host,
                String project,
                String repository) {
        super(steps)
        _id = id
        _svc = "${svc}".toUpperCase()
        _vcsService = "${_svc}" as VcsService
        _scheme = scheme
        _host = host
        _project = project
        _repository = repository
    }

    String commitsUri
    String statusUri

    @Override
    protected void init() {
        setCommitsUri()
        setStatusUri()
    }

    void setCommitsUri() {
        switch (_vcsService) {
            case VcsService.GITHUB:
                commitsUri = "${_scheme}://api.${_host}/repos/${_project}/${_repository}/commits"
                break
            case VcsService.STASH:
                commitsUri = "${_scheme}://${_host}/rest/api/1.0/projects/${_project}/repos/${_repository}/compare/commits"
                break
        }
    }

    void setStatusUri() {
        switch (_vcsService) {
            case VcsService.GITHUB:
                statusUri = "${_scheme}://api.${_host}/repos/${_project}/${_repository}/statuses"
                break
            case VcsService.STASH:
                statusUri = "${_scheme}://${_host}/rest/build-status/1.0/commits"
                break
        }

        statusUri += "/${_steps.pipelineSettings.gitSettings.commit}"
    }

    void notify(int state) {
        switch (_vcsService) {
            case VcsService.GITHUB:
                populateGitHubRequestBody(GitHubState.values()[state])
                break
            case VcsService.STASH:
                populateStashRequestBody(StashState.values()[state])
                break
        }

        post()
    }

    private void populateGitHubRequestBody(GitHubState gitHubState) {
        _requestBody = JsonOutput.toJson(
            [
                state      : gitHubState.toString().toLowerCase(),
                target_url : "${_steps.env.BUILD_URL}".replace("%", "%%"),
                description: "${_steps.pipelineSettings.gitSettings.version}",
                context    : "${_label}"
            ]
        )
    }

    private void populateStashRequestBody(StashState stashState) {
        _requestBody = JsonOutput.toJson(
            [
                state      : stashState,
                key        : "${_steps.pipelineSettings.gitSettings.version}",
                name       : "${_steps.pipelineSettings.gitSettings.version}",
                url        : "${_steps.env.BUILD_URL}".replace("%", "%%"),
                description: "${_label}"
            ]
        )
    }

    private void post() {
        new HttpRequest(
            _steps,
            _id
        ).post(
            HttpRequestContentType.APPLICATION_JSON,
            HttpRequestResponseHandle.NONE,
            _requestBody,
            statusUri
        )
    }
}
