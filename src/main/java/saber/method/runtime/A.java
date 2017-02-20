package saber.method.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by baipeng on 2017/2/20.
 */
public class A<RequestMsg, ResponseMsg> {
    private static final Logger LOGGER = LoggerFactory.getLogger(A.class);
    private static final ExecutorService THREAD_EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private ByteBuffer readBuf = ByteBuffer.allocate(1024);
    private AtomicBoolean lock = new AtomicBoolean(false);

    private SocketChannel channel;
    private IMsgDecoder<RequestMsg> decoder;
    private IProcessor<RequestMsg, ResponseMsg> processor;

    public A(SocketChannel channel, IMsgDecoder<RequestMsg> decoder, IProcessor<RequestMsg, ResponseMsg> processor) {
        this.channel = channel;
        this.decoder = decoder;
        this.processor = processor;
    }

    public boolean readToBuf() {
        if (!lock.compareAndSet(false, true)) {
            return false;
        }

        int size;
        try {
            while ((size = channel.read(readBuf)) > 0) ;

            if (size == -1) {
                return true;
            }
            return readBuf.position() < readBuf.capacity() - 1;
        } catch (IOException e) {
            return false;
        } finally {
            lock.set(false);
            THREAD_EXECUTOR.execute(new Runnable() {
                public void run() {
                    decoder.appendData(read());
                    RequestMsg msg = decoder.next();
                    if (msg != null) {
                        ResponseMsg responseMsg = processor.process(msg);
                        if (responseMsg != null) {

                        }
                    }
                }
            });
        }

    }

    private byte[] read() {
        if (!lock.compareAndSet(false, true)) {
            return null;
        }

        byte[] data = Arrays.copyOf(readBuf.array(), readBuf.position());
        readBuf.clear();
        lock.set(false);
        return data;
    }


}
