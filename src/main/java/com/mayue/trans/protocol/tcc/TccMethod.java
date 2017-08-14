package com.mayue.trans.protocol.tcc;

import com.mayue.trans.protocol.ExecuteOrder;
import com.mayue.trans.protocol.RpcBusinessProvider;

import java.io.Serializable;

/**
 * TCC for Try-Confirm-Cancel
 * 方法均是幂等的
 */
public interface TccMethod<P extends TccMethodRequest<R>, R extends Serializable> extends RpcBusinessProvider<P> {

    /**
     * 此方法不在doConfirm，doCancel后执行
     *
     * @param param
     * @return
     */
    @ExecuteOrder(doNotExecuteAfter = {"doConfirm", "doCancel"}, ifNotExecutedReturnDirectly = {}, isSynchronousMethod = true)
    R doTry(P param);

    /**
     * comfirm
     *
     * @param param
     */
    void doConfirm(P param);

    /**
     * 如果doTry未执行，则fast return
     *
     * @param param
     */
    @ExecuteOrder(doNotExecuteAfter = {}, ifNotExecutedReturnDirectly = {"doTry"})
    void doCancel(P param);
}
