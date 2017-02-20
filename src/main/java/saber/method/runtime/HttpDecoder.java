package saber.method.runtime;

import java.nio.charset.Charset;

/**
 * Created by baipeng on 2017/2/20.
 */
public class HttpDecoder implements IMsgDecoder<HttpRequest> {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private StringBuilder msg = new StringBuilder();

    public void appendData(byte[] data) {
        if (data != null) {
            msg.append(new String(data, UTF8));
        }
    }

    public HttpRequest next() {
        int postStrIndex = msg.indexOf("\r\n\r\n");
        if (postStrIndex < 0) {
            return null;
        }

        String mark = "Content-Length: ";
        int markIndex = msg.indexOf(mark);
        int length = Integer.valueOf(msg.substring(markIndex + mark.length(), msg.indexOf("\r", markIndex)));
        if (msg.substring(postStrIndex + 4).getBytes(UTF8).length < length) {
            return null;
        }

        String method = msg.delete(0,msg.indexOf(" ")).toString();
        String action = msg.delete(msg.indexOf(" ") + 1,msg.lastIndexOf(" ")).toString();

        int lineSplitIndex = msg.indexOf("\r\n");

        return new HttpRequest();
    }
}
