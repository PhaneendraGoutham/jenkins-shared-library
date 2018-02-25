package settings.downstream

import groovy.json.JsonSlurper
import settings.Settings
import steps.httprequest.HttpRequest
import steps.httprequest.HttpRequestContentType
import steps.httprequest.HttpRequestCustomHeaders
import steps.httprequest.HttpRequestResponseHandle

class DownstreamSettings extends Settings {
    private String _id
    private String _scheme
    private String _host
    private String _port
    private List _jobs

    private String _crumb
    private String _crumbRequestField

    DownstreamSettings(def steps,
                       String id,
                       String scheme,
                       String host,
                       String port,
                       String jobs) {
        super(steps)
        _id = id
        _scheme = scheme
        _host = host
        _port = port
        _jobs = jobs.tokenize(';')
    }

    @Override
    protected void init() {
        authenticate()
    }

    void build() {
        String base = "${_scheme}://${_host}:${_port}"
        HttpRequestCustomHeaders customHeaders = new HttpRequestCustomHeaders(
            false,
            _crumbRequestField,
            _crumb
        )

        try {
            for (String job in _jobs) {
                String url = "${base}/${job}".replace("%", "%%")
                new HttpRequest(
                    _steps,
                    _id
                ).post(
                    HttpRequestContentType.APPLICATION_JSON,
                    HttpRequestResponseHandle.NONE,
                    customHeaders,
                    url
                )
            }
        } catch (error) {
            throw error
        }
    }

    private void authenticate() {
        String text = new HttpRequest(
            _steps,
            _id
        ).get(
            HttpRequestContentType.APPLICATION_JSON,
            HttpRequestResponseHandle.NONE,
            "${_scheme}://${_host}:${_port}/crumbIssuer/api/json"
        )

        def jsonSlurper = new JsonSlurper()
        def json = jsonSlurper.parseText("${text}")
        try {
            _crumb = json['crumb']
            _crumbRequestField = json['crumbRequestField']
        } catch (error) {
            throw error
        }
    }
}
