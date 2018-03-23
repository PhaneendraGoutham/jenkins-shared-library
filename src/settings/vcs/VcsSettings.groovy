package settings.vcs

import constants.GitFlowConstants
import constants.ToolConstants
import groovy.json.JsonOutput
import settings.Settings
import steps.httprequest.HttpRequest
import steps.httprequest.HttpRequestContentType
import steps.httprequest.HttpRequestResponseHandle

class VcsSettings extends Settings {
    private final String _label = 'continuous-integration/jenkins'

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
                String project) {
        super(steps)
        _id = id
        _svc = "${svc}".toUpperCase()
        _vcsService = "${_svc}" as VcsService
        _scheme = scheme
        _host = host
        _project = project
    }

    String commitsUri
    String statusUri

    @Override
    protected void init() {
        _repository = _steps.pipelineSettings.gitSettings.repository
        setCommitsUri()
        setStatusUri()
    }

    void setCommitsUri() {
        switch (_vcsService) {
            case [VcsService.BITBUCKET, VcsService.STASH]:
                commitsUri = "${_scheme}://${_host}/rest/api/1.0/projects/${_project}/repos/${_repository}/compare/commits"
                break
            case VcsService.GITHUB:
                commitsUri = "${_scheme}://api.${_host}/repos/${_project}/${_repository}/commits"
                break
        }
    }

    void setStatusUri() {
        switch (_vcsService) {
            case [VcsService.BITBUCKET, VcsService.STASH]:
                statusUri = "${_scheme}://${_host}/rest/build-status/1.0/commits"
                break
            case VcsService.GITHUB:
                statusUri = "${_scheme}://api.${_host}/repos/${_project}/${_repository}/statuses"
                break
        }

        statusUri += "/${_steps.pipelineSettings.gitSettings.commit}"
    }

    void notify(int state) {
        switch (_vcsService) {
            case [VcsService.BITBUCKET, VcsService.STASH]:
                populateBitbucketStashRequestBody(BitbucketStashState.values()[state])
                break
            case VcsService.GITHUB:
                populateGitHubRequestBody(GitHubState.values()[state])
                break
        }

        post()
    }

    void tag() {
        if ("${_steps.BRANCH_NAME}" != GitFlowConstants.MASTER) {
            return
        }

        String tool = ToolConstants.GIT
        String tag = sprintf(
            'tag -a "%1$s" -m "%2$s"',
            [
                _steps.pipelineSettings.gitSettings.version,
                _label
            ])

        try {
            _steps.bat "${tool} ${tag}"
        }
        catch (error) {
            _steps.echo "${error}"
        }

        try {
            _steps.withCredentials([
                _steps.usernamePassword(
                    credentialsId: "${_id}",
                    passwordVariable: 'tagPassword',
                    usernameVariable: 'tagUsername')]) {
                def url = _steps.pipelineSettings.gitSettings.url
                def push = sprintf(
                    'push https://%1$s:%2$s@%3$s --tags',
                    [
                        _steps.env.tagUsername,
                        _steps.env.tagPassword,
                        "${url}".substring("${url}".lastIndexOf("@") + 1)
                    ])
                _steps.bat "${tool} ${push}"
            }
        }
        catch (error) {
            _steps.echo "${error}"
        }
    }

    private void populateBitbucketStashRequestBody(BitbucketStashState bitbucketStashState) {
        _requestBody = JsonOutput.toJson(
            [
                state      : bitbucketStashState,
                key        : "${_steps.pipelineSettings.gitSettings.version}",
                name       : "${_steps.pipelineSettings.gitSettings.version}",
                url        : "${_steps.env.BUILD_URL}",
                description: "${_label}"
            ]
        )
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
