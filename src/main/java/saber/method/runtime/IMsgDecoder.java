package saber.method.runtime;

/**
 * Created by baipeng on 2017/2/20.
 */
public interface IMsgDecoder<Msg> {

    void appendData(byte[] data);

    Msg next();
}
