package nva.commons.handlers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import nva.commons.utils.JacocoGenerated;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestInfo {

    public static final String PROXY_TAG = "proxy";
    private Map<String, String> headers;
    private String path;
    private Map<String, String> pathParameters;
    @JsonProperty("queryStringParameters")
    private Map<String, String> queryParameters;
    private JsonNode requestContext;

    public RequestInfo() {
        this.headers = new HashMap<>();
        this.pathParameters = new HashMap<>();
        this.queryParameters = new HashMap<>();
    }

    @JacocoGenerated
    public Map<String, String> getHeaders() {
        return headers;
    }

    @JacocoGenerated
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @JacocoGenerated
    public String getPath() {
        return path;
    }

    @JacocoGenerated
    public void setPath(String path) {
        this.path = path;
    }

    @JacocoGenerated
    public Map<String, String> getPathParameters() {
        return pathParameters;
    }

    @JacocoGenerated
    public void setPathParameters(Map<String, String> pathParameters) {
        this.pathParameters = pathParameters;
    }

    @JacocoGenerated
    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    @JacocoGenerated
    public void setQueryParameters(Map<String, String> queryParameters) {
        this.queryParameters = queryParameters;
    }

    @JacocoGenerated
    public JsonNode getRequestContext() {
        return requestContext;
    }

    @JacocoGenerated
    public void setRequestContext(JsonNode requestContext) {
        this.requestContext = requestContext;
    }
}

