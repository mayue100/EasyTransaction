package com.mayue.trans.protocol.aft;

import java.io.Serializable;

import com.mayue.trans.protocol.RpcBusinessProvider;

/**
 * 主线程成功执行后，执行的方法接口
 * @param <P>
 * @param <R>
 */
public interface AfterMasterTransMethod<P extends AfterMasterTransRequest<R>, R extends Serializable> extends RpcBusinessProvider<P> {
	R afterTransaction(P param);
}
