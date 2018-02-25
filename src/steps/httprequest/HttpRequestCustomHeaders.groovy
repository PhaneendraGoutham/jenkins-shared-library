package steps.httprequest

class HttpRequestCustomHeaders {
    HttpRequestCustomHeaders(boolean maskValue,
                             String name,
                             String value) {
        this.maskValue = maskValue
        this.name = name
        this.value = value
    }

    boolean maskValue
    String name
    String value
}
