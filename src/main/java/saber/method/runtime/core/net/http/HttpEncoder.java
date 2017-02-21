package saber.method.runtime.core.net.http;

import saber.method.runtime.core.net.IMsgEncoder;

import java.util.Map;

/**
 * Created by baipeng on 2017/2/21.
 */
public class HttpEncoder implements IMsgEncoder<HttpResponse> {

    //TODO:待解析charset信息,默认使用UTF-8
    public byte[] encode(HttpResponse httpResponse) {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ");
        response.append(httpResponse.getStatusCode());
        response.append("\r\n");

        if (httpResponse.getHeaders() != null) {
            for (Map.Entry<String, String> headers : httpResponse.getHeaders().entrySet()) {
                response.append(headers.getKey());
                response.append(": ");
                response.append(headers.getValue());
                response.append("\r\n");
            }
        }

        if (httpResponse.getContent() != null) {
            response.append("Content-Length: " + httpResponse.getContent().getBytes(HttpDecoder.UTF8).length);
            response.append("\r\n\r\n");
            response.append(httpResponse.getContent());
        } else {
            response.append("\r\n");
        }
        return response.toString().getBytes(HttpDecoder.UTF8);
    }
}
