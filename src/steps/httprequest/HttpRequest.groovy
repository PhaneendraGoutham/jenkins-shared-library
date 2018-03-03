package steps.httprequest

class HttpRequest implements Serializable {
    private def _steps
    private String _authentication

    private final String DEFAULT_VALID_RESPONSE_CODES = '100:399'

    HttpRequest(def steps,
                String authentication) {
        _steps = steps
        _authentication = authentication
    }

    void delete(HttpRequestResponseHandle responseHandle,
                String validResponseCodes = DEFAULT_VALID_RESPONSE_CODES) {

    }

    String get(HttpRequestContentType contentType,
               HttpRequestResponseHandle responseHandle,
               String url,
               String validResponseCodes = DEFAULT_VALID_RESPONSE_CODES) {
        try {
            def response = _steps.httpRequest authentication: _authentication,
                consoleLogResponseBody: true,
                contentType: contentType.toString(),
                httpMode: HttpRequestMode.GET.toString(),
                responseHandle: "${responseHandle}",
                url: "${url}",
                validResponseCodes: validResponseCodes
            String content = response.getContent()
            return content
        } catch (error) {
            throw error
        }
    }

    void post(HttpRequestContentType contentType,
              HttpRequestResponseHandle responseHandle,
              String requestBody,
              String url,
              String validResponseCodes = DEFAULT_VALID_RESPONSE_CODES) {
        try {
            _steps.httpRequest authentication: _authentication,
                consoleLogResponseBody: true,
                contentType: contentType.toString(),
                httpMode: HttpRequestMode.POST.toString(),
                requestBody: "${requestBody}",
                responseHandle: "${responseHandle}",
                url: "${url}",
                validResponseCodes: validResponseCodes
        } catch (error) {
            throw error
        }
    }

    void post(HttpRequestContentType contentType,
              HttpRequestResponseHandle responseHandle,
              HttpRequestCustomHeaders customHeaders,
              String url,
              String validResponseCodes = DEFAULT_VALID_RESPONSE_CODES) {
        try {
            _steps.httpRequest authentication: _authentication,
                consoleLogResponseBody: true,
                contentType: contentType.toString(),
                httpMode: HttpRequestMode.POST.toString(),
                customHeaders: [[maskValue: customHeaders.maskValue, name: customHeaders.name, value: customHeaders.value]],
                responseHandle: "${responseHandle}",
                url: "${url}",
                validResponseCodes: validResponseCodes
        } catch (error) {
            throw error
        }
    }

    void put(HttpRequestContentType contentType,
             HttpRequestResponseHandle responseHandle,
             String url,
             String validResponseCodes = DEFAULT_VALID_RESPONSE_CODES) {
        try {
            _steps.httpRequest authentication: _authentication,
                consoleLogResponseBody: true,
                contentType: contentType.toString(),
                httpMode: HttpRequestMode.PUT.toString(),
                responseHandle: "${responseHandle}",
                url: "${url}",
                validResponseCodes: validResponseCodes
        } catch (error) {
            throw error
        }
    }
}
