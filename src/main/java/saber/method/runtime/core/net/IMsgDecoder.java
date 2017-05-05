package saber.method.runtime.core.net;

/**
 * Created by baipeng on 2017/2/20.
 */
public interface IMsgDecoder<Msg> {

    void appendData(byte[] data);

    Msg nextFrame();
}
