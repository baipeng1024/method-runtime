package saber.method.runtime.core.net.http;

import com.alibaba.fastjson.JSON;

import java.util.Map;

/**
 * Created by baipeng on 2017/2/20.
 */
public class HttpRequest {
    private String method;
    private String action;
    private Map<String, String> headers;
    private Map<String, String> postPars;
    private Map<String, String> queryPars;


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, String> getQueryPars() {
        return queryPars;
    }

    public void setQueryPars(Map<String, String> queryPars) {
        this.queryPars = queryPars;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getPostPars() {
        return postPars;
    }

    public void setPostPars(Map<String, String> postPars) {
        this.postPars = postPars;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
