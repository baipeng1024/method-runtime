package saber.method.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by baipeng on 2017/2/18.
 */
public class SimpleServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleServer.class);

    private volatile boolean isRunning = true;

    private int port;
    private String ip;

    private Selector selector;
    private ServerSocketChannel serverChannel;

    private ExecutorService singleThreadExecutor;

    public SimpleServer(String ip, int port) {
        this.port = port;
        if (ip == null || ip.isEmpty()) {
            ip = "0.0.0.0";
        }
        this.ip = ip;

        singleThreadExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                t.setName("SimpleServer:" + this.toString());
                return t;
            }
        });
    }

    public boolean start() {
        try {
            selector = Selector.open();

            serverChannel = ServerSocketChannel.open();
            serverChannel.socket().bind(new InetSocketAddress(InetAddress.getByName(ip), port));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            singleThreadExecutor.execute(new Runnable() {
                public void run() {
                    loop();
                }
            });
            LOGGER.info("Server start ok,listen address:" + this.toString());
        } catch (IOException e) {
            LOGGER.error("Server start error.listen address:" + this.toString(), e);
        }
        return false;
    }

    public void stop() {
        this.isRunning = false;
    }

    private void loop() {
        while (isRunning) {
            try {
                if (selector.select() == 0) continue;
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isAcceptable()) {
                        SocketChannel client = serverChannel.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ,null);
                        LOGGER.info("A new connection,client:" + client.socket().toString());
                        keyIterator.remove();
                    } else if (key.isReadable()) {
                        A a = (A) key.attachment();
                        if (a.readToBuf()) {
                            keyIterator.remove();
                        }
                    }
                }

            } catch (IOException e) {
                LOGGER.error("Server select error.listen address:" + this.toString() + ",error:", e);
            }
        }

        try {
            selector.close();
            serverChannel.close();
        } catch (IOException e) {

        }
        singleThreadExecutor.shutdown();
    }


    @Override
    public String toString() {
        return String.format("ip:%s,port:%s", ip, port);
    }
}
