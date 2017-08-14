package com.mayue.trans.protocol.cps;

import java.io.Serializable;

import com.mayue.trans.protocol.RpcBusinessProvider;
import com.mayue.trans.protocol.ExecuteOrder;

/**
 * 补偿型操作接口
 * @param <P> 补偿型实体
 * @param <R> 序列化
 */
public interface CompensableMethod<P extends CompensableMethodRequest<R>, R extends Serializable> extends RpcBusinessProvider<P> {
	/**
	 * 补偿型业务操作，compensation执行成功后，doCompensableBusiness不执行
	 * @param param 入参
	 * @return 出参
	 */
	@ExecuteOrder(doNotExecuteAfter = { "compensation" }, ifNotExecutedReturnDirectly = {}, isSynchronousMethod=true)
	R doCompensableBusiness(P param);
	/**
	 * 补偿操作，如doCompensableBusiness未执行，则直接返回
	 * @param param
	 */
	@ExecuteOrder(doNotExecuteAfter = {}, ifNotExecutedReturnDirectly = {"doCompensableBusiness"})
    void compensation(P param);
}
