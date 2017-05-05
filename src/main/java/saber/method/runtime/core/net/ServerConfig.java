package saber.method.runtime.core.net;

/**
 * Created by baipeng on 2017/2/21.
 */
public class ServerConfig<RequestMsg, ResponseMsg> {

    private String ip;

    private int port;

    private Class<IProcessor<RequestMsg, ResponseMsg>> processorClass;

    private Class<IMsgDecoder<RequestMsg>> decoderClass;

    private Class<IMsgEncoder<ResponseMsg>> encoderClass;

    public ServerConfig(String ip, int port, Class<IMsgDecoder<RequestMsg>> decoderClass, Class<IMsgEncoder<ResponseMsg>> encoderClass, Class<IProcessor<RequestMsg, ResponseMsg>> processorClass) {
        this.ip = ip;
        this.port = port;
        this.decoderClass = decoderClass;
        this.encoderClass = encoderClass;
        this.processorClass = processorClass;
    }

    public IProcessor<RequestMsg, ResponseMsg> buildProcessor() throws IllegalAccessException, InstantiationException {
        return processorClass.newInstance();
    }

    public IMsgDecoder<RequestMsg> buildDecoder() throws IllegalAccessException, InstantiationException {
        return decoderClass.newInstance();
    }

    public IMsgEncoder<ResponseMsg> buildEncoder() throws IllegalAccessException, InstantiationException {
        return encoderClass.newInstance();
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
