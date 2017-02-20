package saber.method.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by baipeng on 2017/2/18.
 */
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String args[]) throws InterruptedException {
        SimpleServer server = new SimpleServer("127.0.0.1",20000);
        server.start();
        Thread.sleep(100000);
    }

    }
