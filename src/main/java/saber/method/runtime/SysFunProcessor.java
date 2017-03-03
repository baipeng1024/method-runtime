package saber.method.runtime;

import com.alibaba.fastjson.JSON;
import saber.method.runtime.core.net.http.HttpRequest;
import saber.method.runtime.core.net.http.HttpResponse;
import saber.method.runtime.core.net.IProcessor;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by baipeng on 2017/2/21.
 */
public class SysFunProcessor implements IProcessor<HttpRequest, HttpResponse> {

    private static final class Result {
        boolean isOk;
        Object obj;
        String errorMsg = "";
    }

    private static final class Expression {
        String fun;
        Object[] pars;

        public void setFun(String fun) {
            this.fun = fun;
        }

        public void setPars(Object[] pars) {
            this.pars = pars;
        }
    }

    private static final HashMap<String, Method> METHOD_CACHE = new HashMap<String, Method>();

    public HttpResponse process(HttpRequest httpRequest) {
        HttpResponse response = new HttpResponse();
        Map<String, String> responseHeaders = new HashMap<String, String>();
        response.setHeaders(responseHeaders);

        response.setStatusCode(HttpResponse.STATUS_SUCCESS);
        responseHeaders.put("Content-Type", "text/html; charset=UTF-8");
        responseHeaders.put("Access-Control-Allow-Origin", "*");

        Result result = new Result();
        Map<String, String> queryPars = httpRequest.getQueryPars();
        if (queryPars == null || !queryPars.containsKey("expr")) {
            result.isOk = false;
            result.errorMsg = "缺少请求参数:expr.";
        } else {
            Expression expr = null;
            try {
                expr = JSON.parseObject(queryPars.get("expr"), Expression.class);
            } catch (Exception e) {
                result.isOk = false;
                result.errorMsg = "解析表达式异常，异常信息:" + e.getMessage();
            }
            if (expr != null) {
                callFun(result, expr);
            }
        }

        String objStr = "\"\"";
        if (result.obj != null) {
            objStr = result.obj instanceof String ? "\"" + result.obj + "\"" : result.obj.toString();
        }
        String content = "var $sysFunResult = { isOk:" + (result.isOk ? "true" : "false") + //
                ",result:" + objStr + //
                ",errorMsg:\"" + result.errorMsg + "\"" + //
                "};";
        response.setContent(content);
        return response;
    }

    private void callFun(Result result, Expression expr) {

        int parsCount = expr.pars == null ? 0 : expr.pars.length;
        String key = expr.fun + "@" + parsCount;
        Method method = METHOD_CACHE.get(key);
        if (method != null) {
            try {
                result.obj = method.invoke(null, expr.pars);
                result.isOk = true;
            } catch (Exception e) {
                result.isOk = false;
                result.errorMsg = e.getMessage();
            }
            return;
        }

        int splitIndex = expr.fun.lastIndexOf(".");
        if (splitIndex < 0) {
            result.isOk = false;
            result.errorMsg = "方法名称格式要求:包名.方法名.";
            return;
        }
        String className = expr.fun.substring(0, splitIndex);
        String methodName = expr.fun.substring(splitIndex + 1);

        try {
            Class c = Class.forName(className);
            for (Method m : c.getMethods()) {
                if (m.getName().equals(methodName) && m.getParameterTypes().length == parsCount) {
                    method = m;
                    METHOD_CACHE.put(key, m);
                }
            }

            if (method == null) {
                result.isOk = false;
                result.errorMsg = "没有找到目标函数";
                return;
            }
            result.obj = method.invoke(null, expr.pars);
            result.isOk = true;

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
