package com.mayue.trans.protocol.msg;

import com.mayue.trans.protocol.EasyTransRequest;
import com.mayue.trans.executor.BestEffortMessageMethodExecutor;
import com.mayue.trans.protocol.msg.BestEffortMessagePublishRequest.SerializableVoid;

/**
 *	Best effort message
 */
public interface BestEffortMessagePublishRequest extends EasyTransRequest<SerializableVoid,BestEffortMessageMethodExecutor> {
	
	public static enum SerializableVoid {
		SINGLETON
	}
	
}
