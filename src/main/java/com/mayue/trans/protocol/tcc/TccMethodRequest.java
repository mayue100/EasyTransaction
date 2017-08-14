package com.mayue.trans.protocol.tcc;

import java.io.Serializable;

import com.mayue.trans.protocol.EasyTransRequest;
import com.mayue.trans.executor.TccMethodExecutor;

/**
 * tcc 操作接口
 * @param <R>
 */
public interface TccMethodRequest<R extends Serializable> extends EasyTransRequest<R,TccMethodExecutor> {
	
}
