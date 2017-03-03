package saber.method.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import saber.method.runtime.core.net.http.HttpDecoder;
import saber.method.runtime.core.net.Server;
import saber.method.runtime.core.net.ServerConfig;
import saber.method.runtime.core.net.http.HttpEncoder;

/**
 * Created by baipeng on 2017/2/18.
 */
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String args[]) throws InterruptedException {
        ServerConfig httpServerConf = new ServerConfig("127.0.0.1", 20000, HttpDecoder.class, HttpEncoder.class,SysFunProcessor.class);
        Server server = new Server(httpServerConf);
        server.start();
        Thread.sleep(10000000);
    }

}
