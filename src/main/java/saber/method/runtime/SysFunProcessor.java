package saber.method.runtime;

import saber.method.runtime.core.net.http.HttpRequest;
import saber.method.runtime.core.net.http.HttpResponse;
import saber.method.runtime.core.net.IProcessor;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by baipeng on 2017/2/21.
 */
public class SysFunProcessor implements IProcessor<HttpRequest, HttpResponse> {

    private static final class Result {
        boolean isOk;
        Object obj;
        String errorMsg = "";
    }

    private static final class Par{
        String type;
        Object val;
    }

    private static final HashMap<String, Method> CLASS_MAP = new HashMap<String, Method>();

    public HttpResponse process(HttpRequest httpRequest) {
        HttpResponse response = new HttpResponse();
        Map<String, String> responseHeaders = new HashMap<String, String>();
        response.setStatusCode(HttpResponse.STATUS_SUCCESS);
        responseHeaders.put("text/plain", "text/javascript; charset=UTF-8");

        Result result = new Result();
        Map<String, String> queryPars = httpRequest.getQueryPars();
        if (queryPars == null || !queryPars.containsKey("fun")) {
            result.isOk = false;
            result.errorMsg = "缺少请求参数:fun.";
        } else {
            String fun = queryPars.get("fun");
            String pars = queryPars.get("pars");
            callFun(result, fun, pars);
        }

        String objStr = "";
        if (result.obj != null) {
            objStr = result.obj instanceof String ? "\"" + result.obj + "\"" : result.obj.toString();
        }
        String content = "var $sysFunResult = { isOk:" + (result.isOk ? "true" : "false") + //
                ",result:" + objStr + //
                ",errorMsg" + result.errorMsg + //
                "};";
        response.setContent(content);
        return response;
    }

    private void callFun(Result result, String fun, String pars) {
        int parCount = pars == null ? 0 : pars.split(",");

        int splitIndex = fun.lastIndexOf(".");
        if (splitIndex < 0) {
            result.isOk = false;
            result.errorMsg = "表达式格式错误.";
            return;
        }
        try {
            String className = fun.substring(0, splitIndex);
            String funName = fun.substring(splitIndex - 1);

            Class targetClass = CLASS_MAP.get(className);
            if (targetClass == null) {
                targetClass = loadClass(className);
                CLASS_MAP.put(className,targetClass);
            }

            Method method = targetClass.getMethod("",T);
            method.in
        } catch (Exception e) {
            result.isOk = false;
            result.errorMsg = "系统函数调用异常,异常信息:" + e.getMessage();
        }

    }

    private Class loadClass(String name) throws ClassNotFoundException, MalformedURLException {
        File file = new File("c:\\myjar.jar");
        URL[] urls = new URL[]{file.toURI().toURL()};
        ClassLoader cl = new URLClassLoader(urls);
        return cl.loadClass(name);
    }

}
