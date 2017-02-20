package saber.method.runtime;

/**
 * Created by baipeng on 2017/2/20.
 */
public interface IProcessor<RequestMsg,ResponseMsg> {
    ResponseMsg process(RequestMsg msg);
}
