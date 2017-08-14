package com.mayue.trans.protocol.cps;

import java.io.Serializable;

import com.mayue.trans.protocol.EasyTransRequest;
import com.mayue.trans.executor.CompensableMethodExecutor;

/**
 * 补偿型实体实现此接口
 * @param <R>  序列化实体
 */
public interface CompensableMethodRequest<R extends Serializable> extends EasyTransRequest<R,CompensableMethodExecutor> {
}
