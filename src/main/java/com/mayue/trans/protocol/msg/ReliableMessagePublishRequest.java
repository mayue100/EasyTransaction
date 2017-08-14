package com.mayue.trans.protocol.msg;

import com.mayue.trans.protocol.EasyTransRequest;
import com.mayue.trans.executor.ReliableMessageMethodExecutor;

/**
 *	transaction message
 */
public interface ReliableMessagePublishRequest extends EasyTransRequest<PublishResult,ReliableMessageMethodExecutor> {
}
