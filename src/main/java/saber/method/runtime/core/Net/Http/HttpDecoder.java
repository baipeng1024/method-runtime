package saber.method.runtime.core.net.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import saber.method.runtime.core.net.IMsgDecoder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by baipeng on 2017/2/20.
 */
public class HttpDecoder implements IMsgDecoder<HttpRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpDecoder.class);
    public static final Charset UTF8 = Charset.forName("UTF-8");

    private StringBuilder msg = new StringBuilder();

    public void appendData(byte[] data) {
        if (data != null) {
            msg.append(new String(data, UTF8));
        }
    }

    //TODO:待解析charset信息,默认使用UTF-8
    public HttpRequest nextFrame() {
        int postStrIndex = msg.indexOf("\r\n\r\n");
        if (postStrIndex < 0) {
            return null;
        }

        String mark = "Content-Length: ";
        int markIndex = msg.indexOf(mark);
        if (markIndex > -1) {
            int length = Integer.valueOf(msg.substring(markIndex + mark.length(), msg.indexOf("\r", markIndex)));
            if (msg.substring(postStrIndex + 4).getBytes(UTF8).length < length) {
                return null;
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("requestMsg:" + msg.toString());
        }

        int spaceIndex = msg.indexOf(" ");
        String method = msg.substring(0, spaceIndex);
        String url = msg.substring(spaceIndex + 1, msg.indexOf(" ", spaceIndex + 1)).toString();
        String action;
        Map<String, String> queryPasMap = null;
        int queryMarkIndex = url.indexOf("?");
        if (queryMarkIndex > -1) {
            action = url.substring(0, queryMarkIndex);
            if (queryMarkIndex < url.length() - 1) {
                queryPasMap = getKVPars(url.substring(queryMarkIndex + 1));

            }
        } else {
            action = url;
        }

        int headerBeginIndex = msg.indexOf("\r\n") + 2;
        int headerEndIndex = msg.indexOf("\r\n\r\n");
        String headers = msg.substring(headerBeginIndex, headerEndIndex);
        String[] headerArr = headers.split("\r\n");
        HashMap<String, String> headerMap = new HashMap<String, String>(headerArr.length);
        for (String header : headerArr) {
            String[] kv = header.split(": ");
            headerMap.put(kv[0], kv[1]);
        }

        HttpRequest request = new HttpRequest();
        request.setMethod(method);
        request.setAction(action);
        request.setQueryPars(queryPasMap);
        request.setHeaders(headerMap);
        int contentIndex = headerEndIndex + 4;
        if (contentIndex < msg.length()) {
            request.setPostPars(getKVPars(msg.substring(contentIndex)));
        }
        return request;
    }

    private Map<String, String> getKVPars(String parsStr) {
        String[] queryPars = parsStr.split("&");
        Map<String, String> kvPars = new HashMap<String, String>(queryPars.length);
        for (String par : queryPars) {
            String[] kv = par.split("=");
            try {
                kvPars.put(kv[0], URLDecoder.decode(kv[1], "utf-8"));
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("URLDecoder.decode error.", e);
            }
        }
        return kvPars;
    }
}
