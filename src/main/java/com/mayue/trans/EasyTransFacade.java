package com.mayue.trans;

import java.io.Serializable;
import java.util.concurrent.Future;

import com.mayue.trans.executor.EasyTransExecutor;
import com.mayue.trans.protocol.EasyTransRequest;

public interface EasyTransFacade {
	
	public void startEasyTrans(String busCode,String trxId);

	public <P extends EasyTransRequest<R,E>,E extends EasyTransExecutor, R extends Serializable> Future<R> execute(P params);
}
