package com.mayue.trans.executor;

import java.io.Serializable;
import java.util.concurrent.Future;

import com.mayue.trans.protocol.EasyTransRequest;

/**
 * 最上层执行器
 */
public interface EasyTransExecutor{
	<P extends EasyTransRequest<R,E>,E extends EasyTransExecutor,R  extends Serializable> Future<R> execute(P params);
}
