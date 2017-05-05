package saber.method.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import saber.method.runtime.core.net.Server;
import saber.method.runtime.core.net.ServerConfig;
import saber.method.runtime.core.net.http.HttpDecoder;
import saber.method.runtime.core.net.http.HttpEncoder;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by baipeng on 2017/2/18.
 */
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String args[]) throws InterruptedException, MalformedURLException, ClassNotFoundException {
        String port = "20000";
        args = new String[]{"20000", "C:\\Users\\baipeng\\工作\\system-function\\target"};
        if (args != null) {
            if (args.length > 0) {
                port = args[0];
            }
            if (args.length > 1) {
                File file = new File(args[1]);
                for (String fileName : file.list()) {
                    int index = fileName.lastIndexOf(".");
                    if (index > -1) {
                        String suffix = fileName.substring(index);
                        if (suffix.equals(".jar")) {
                            ClassLoader cl = new URLClassLoader(new URL[]{new File(fileName).toURI().toURL()});
                            cl.loadClass("*");
                        }
                    }
                }
            }
        }

        ServerConfig httpServerConf = new ServerConfig("127.0.0.1", Integer.valueOf(port), HttpDecoder.class, HttpEncoder.class, SysFunProcessor.class);
        Server server = new Server(httpServerConf);
        server.start();
    }

}
