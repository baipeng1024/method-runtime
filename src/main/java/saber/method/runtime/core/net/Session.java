package saber.method.runtime.core.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by baipeng on 2017/2/20.
 */
public class Session<RequestMsg, ResponseMsg> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Session.class);
    private static final ExecutorService THREAD_EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
        AtomicInteger number = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("session_" + number.getAndIncrement());
            return t;
        }
    });

    private ByteBuffer readBuf = ByteBuffer.allocate(1024);
    private ByteBuffer writeBuf = ByteBuffer.allocate(1024);
    private AtomicBoolean lock = new AtomicBoolean(false);

    private SocketChannel channel;
    private IMsgDecoder<RequestMsg> decoder;
    private IMsgEncoder<ResponseMsg> encoder;
    private IProcessor<RequestMsg, ResponseMsg> processor;

    public Session(SocketChannel channel, IMsgDecoder<RequestMsg> decoder, IMsgEncoder<ResponseMsg> encoder, IProcessor<RequestMsg, ResponseMsg> processor) {
        this.channel = channel;
        this.decoder = decoder;
        this.encoder = encoder;
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
                    RequestMsg msg = decoder.nextFrame();
                    if (msg != null) {
                        ResponseMsg responseMsg = processor.process(msg);
                        if (responseMsg != null) {
                            byte[] data = encoder.encode(responseMsg);
                            int offset = 0;
                            while (offset < data.length) {
                                writeBuf.clear();
                                int length = data.length - offset;
                                if (length > writeBuf.capacity()) {
                                    length = writeBuf.capacity();
                                }

                                writeBuf.put(data, offset, length);
                                offset += length;
                                writeBuf.flip();
                                while (writeBuf.remaining() > 0) {
                                    try {
                                        channel.write(writeBuf);
                                    } catch (IOException e) {
                                        return;
                                    }
                                }
                            }

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
