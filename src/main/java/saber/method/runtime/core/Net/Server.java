package saber.method.runtime.core.net;

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
public class Server<RequestMsg, ResponseMsg> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private volatile boolean isRunning = true;

    private ServerConfig<RequestMsg, ResponseMsg> serverConfig;

    private Selector selector;
    private ServerSocketChannel serverChannel;

    private ExecutorService singleThreadExecutor;

    public Server(ServerConfig<RequestMsg, ResponseMsg> serverConfig) {
        this.serverConfig = serverConfig;
    }

    public boolean start() {
        try {
            String ip = serverConfig.getIp();
            if (ip == null || ip.isEmpty()) {
                ip = "0.0.0.0";
            }

            selector = Selector.open();

            serverChannel = ServerSocketChannel.open();
            serverChannel.socket().bind(new InetSocketAddress(InetAddress.getByName(ip), serverConfig.getPort()));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            singleThreadExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    t.setName("server_port:" + serverConfig.getPort());
                    return t;
                }
            });

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
                        client.register(selector, SelectionKey.OP_READ, new Session(client, serverConfig.buildDecoder(),serverConfig.buildEncoder(),serverConfig.buildProcessor()));
                        LOGGER.info("A new connection,client:" + client.socket().toString());
                        keyIterator.remove();
                    } else if (key.isReadable()) {
                        Session a = (Session) key.attachment();
                        if (a.readToBuf()) {
                            keyIterator.remove();
                        }
                    }
                }

            } catch (Exception e) {
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
        return String.format("ip:%s,port:%s", serverConfig.getIp(), serverConfig.getPort());
    }
}
