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

    String response

    void delete(HttpRequestResponseHandle responseHandle,
                String validResponseCodes = DEFAULT_VALID_RESPONSE_CODES) {

    }

    String get(HttpRequestResponseHandle responseHandle,
               String validResponseCodes = DEFAULT_VALID_RESPONSE_CODES) {

    }

    void post(HttpRequestContentType contentType,
              HttpRequestResponseHandle responseHandle,
              String requestBody,
              String url,
              String validResponseCodes = DEFAULT_VALID_RESPONSE_CODES) {
        try {
            _steps.httpRequest authentication: _authentication,
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

    void put(HttpRequestResponseHandle responseHandle,
             String validResponseCodes = DEFAULT_VALID_RESPONSE_CODES) {

    }
}
