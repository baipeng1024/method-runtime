package saber.method.runtime.core.net.http;

import java.util.Map;

/**
 * Created by baipeng on 2017/2/21.
 */
public class HttpResponse {

    public static int STATUS_SUCCESS = 200;
    public static int STATUS_NOT_FOUND = 404;
    public static int STATUS_SERVER_ERROR = 500;

    private int statusCode;
    private Map<String, String> headers;
    private String content;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
