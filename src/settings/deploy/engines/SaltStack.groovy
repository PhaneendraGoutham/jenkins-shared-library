package settings.deploy.engines

import groovy.json.JsonOutput
import settings.deploy.DeployItem
import settings.publish.PublishArtifactType
import settings.publish.PublishItem
import steps.httprequest.HttpRequest
import steps.httprequest.HttpRequestContentType
import steps.httprequest.HttpRequestResponseHandle

class SaltStack extends DeployEngine {
    private final String CLIENT = 'local'
    private final String EAUTH = 'pam'

    private String _id
    private String _scheme
    private String _host
    private String _port
    private String _pillarenv
    private String _saltenv
    private String _data
    private List<Map> _highstates

    private String _url
    private Map<PublishArtifactType, Boolean> _publishParams = [:]
    private List<PublishItem> _publishItems = []

    private String _password
    private String _username
    private HttpRequest _httpRequest

    SaltStack(def steps, DeployItem deployItem) {
        super(steps, deployItem)
        _id = deployItem.info['id']
        _scheme = deployItem.info['scheme']
        _host = deployItem.info['host']
        _port = deployItem.info['port']
        _pillarenv = deployItem.info['pillarenv']
        _saltenv = deployItem.info['saltenv']
        _data = deployItem.info['data']
        _highstates = deployItem.info['highstates'].collect()

        _url = sprintf(
            '%1$s://%2$s:%3$s/%4$s',
            [
                _scheme,
                _host,
                _port,
                "run"
            ])
        _publishParams = _steps.pipelineSettings.publishSettings.publishParams
        _publishItems = _steps.pipelineSettings.publishSettings.publishItems
    }

    @Override
    void install() {
        setup()
        wget()
        apply()
    }

    private void setup() {
        _steps.withCredentials([
            _steps.usernamePassword(
                credentialsId: "${_id}",
                passwordVariable: 'saltpassword',
                usernameVariable: 'saltusername'
            )
        ]) {
            _password = _steps.env.saltpassword
            _username = _steps.env.saltusername
        }

        _httpRequest = new HttpRequest(
            _steps,
            _id
        )
    }

    private void wget() {
        for (PublishItem publishItem in _publishItems) {
            if (!publishItem.isPublish) {
                continue
            }

            String remove = JsonOutput.toJson(
                [
                    client  : CLIENT,
                    eauth   : EAUTH,
                    password: _password,
                    username: _username,
                    tgt     : _host,
                    fun     : 'cmd.run',
                    arg     : sprintf(
                        'rm -f %1$s/%2$s',
                        [
                            _data,
                            publishItem.zipFile.getName().replace("${_steps.pipelineSettings.gitSettings.version}", "*")
                        ]
                    )
                ]
            )
            _httpRequest.post(
                true,
                HttpRequestContentType.APPLICATION_JSON,
                HttpRequestResponseHandle.NONE,
                remove,
                _url
            )

            String wget = JsonOutput.toJson(
                [
                    client  : CLIENT,
                    eauth   : EAUTH,
                    password: _password,
                    username: _username,
                    tgt     : _host,
                    fun     : 'cmd.run',
                    arg     : sprintf(
                        'wget -P %1$s %2$s',
                        [
                            _data,
                            publishItem.artifactUrl
                        ]
                    )
                ]
            )
            _httpRequest.post(
                true,
                HttpRequestContentType.APPLICATION_JSON,
                HttpRequestResponseHandle.NONE,
                wget,
                _url
            )
        }
    }

    private void apply() {
        for (Map highstate in _highstates) {
            String depends = "${highstate['depends']}".toUpperCase()
            PublishArtifactType publishArtifactType = "${depends}" as PublishArtifactType
            if (!_publishParams[publishArtifactType]) {
                continue
            }

            List<String> minions = "${highstate['minions']}".tokenize(';')
            for (String minion in minions) {
                String apply = JsonOutput.toJson(
                    [
                        client  : CLIENT,
                        eauth   : EAUTH,
                        password: _password,
                        username: _username,
                        tgt     : minion,
                        fun     : 'state.apply',
                        arg     : highstate['state'],
                        kwarg   : [
                            pillarenv: _pillarenv,
                            saltenv  : _saltenv,
                            pillar   : [
                                (highstate['pillar']): "${_steps.pipelineSettings.gitSettings.version}"
                            ]
                        ]
                    ]
                )
                _httpRequest.post(
                    true,
                    HttpRequestContentType.APPLICATION_JSON,
                    HttpRequestResponseHandle.NONE,
                    apply,
                    _url
                )
            }
        }
    }
}
