package settings.vcs

import settings.Settings
import settings.vcs.rest.VcsRest
import settings.vcs.rest.VcsService
import settings.vcs.states.GitHubState
import settings.vcs.states.StashState

class VcsSettings extends Settings {
    private final String _label = "Status updated by Jenkins Automation Server."

    private VcsService _vcsService
    private String _scheme
    private String _host
    private String _project
    private String _repository
    private String _username
    private String _password

    private def _data

    VcsSettings(def steps,
                String svc,
                String scheme,
                String host,
                String project,
                String repository,
                String username,
                String password) {
        super(steps)
        VcsService vcsService = svc.toUpperCase()
        _vcsService = vcsService
        _scheme = scheme
        _host = host
        _project = project
        _repository = repository
        _username = username
        _password = password
    }

    static VcsRest vcsRest = VcsRest.getInstance()

    @Override
    protected void init() {
        vcsRest.vcsService = _vcsService
        vcsRest.scheme = _scheme
        vcsRest.host = _host
        vcsRest.project = _project
        vcsRest.repository = _repository
    }

    void notify(int state) {
        switch(_vcsService) {
            case VcsService.GITHUB:
                populateGitHubData(GitHubState.values()[state])
                break
            case VcsService.STASH:
                populateStashData(StashState.values()[state])
                break
        }

        post()
    }

    private void populateGitHubData(GitHubState gitHubState) {
        _data = sprintf(
            '{\\"state\\" : \\"%1$s\\", \\"target_url\\" : \\"%2$s\\", \\"description\\" : \\"%3$s\\", \\"context\\" : \\"%4$s\\"}',
            [
                gitHubState.toString().toLowerCase(),
                "${vcsRest.getStatusUri()}",
                "${steps.pipelineSettings.gitSettings.version}",
                "${_label}"
            ])
    }

    private void populateStashData(StashState stashState) {
        _data = sprintf(
            '{\\"state\\" : \\"%1$s\\", \\"key\\" : \\"%2$s\\", \\"name\\" : \\"%2$s\\", \\"url\\" : \\"%3$s\\", \\"description\\" : \\"%4$s\\"}',
            [
                stashState,
                "${steps.pipelineSettings.gitSettings.version}",
                "${vcsRest.getStatusUri()}",
                "${_label}"
            ])
    }

    private void post() {

    }
}
