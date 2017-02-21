package saber.method.runtime.core.net;

/**
 * Created by baipeng on 2017/2/21.
 */
public interface IMsgEncoder<Msg> {
    byte[] encode(Msg msg);
}
