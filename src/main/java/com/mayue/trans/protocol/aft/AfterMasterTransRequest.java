package com.mayue.trans.protocol.aft;

import java.io.Serializable;

import com.mayue.trans.protocol.EasyTransRequest;
import com.mayue.trans.executor.AfterTransMethodExecutor;

/**
 * 主线程处理完成后执行
 * @param <R>
 */
public interface AfterMasterTransRequest<R extends Serializable> extends EasyTransRequest<R,AfterTransMethodExecutor> {
	
}
