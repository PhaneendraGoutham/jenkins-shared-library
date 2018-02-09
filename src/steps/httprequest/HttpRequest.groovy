package steps.httprequest

class HttpRequest implements Serializable {
    def _steps
    String _authentication

    HttpRequest(def steps,
                String authentication) {
        _steps = steps
        _authentication = authentication
    }

    String response

    void delete(HttpRequestResponseHandle responseHandle,
                String validResponseCodes = '100:399') {

    }

    String get(HttpRequestResponseHandle responseHandle,
               String validResponseCodes = '100:399') {

    }

    void post(HttpRequestResponseHandle responseHandle,
              String validResponseCodes = '100:399') {

    }

    void put(HttpRequestResponseHandle responseHandle,
             String validResponseCodes = '100:399') {

    }
}
