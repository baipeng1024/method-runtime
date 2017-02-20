package saber.method.runtime;

import java.util.List;
import java.util.Map;

/**
 * Created by baipeng on 2017/2/20.
 */
public class HttpRequest {
    private String type;
    private String action;
    private Map<String, String> pars;
    private Map<String, String> headers;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, String> getPars() {
        return pars;
    }

    public void setPars(Map<String, String> pars) {
        this.pars = pars;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
