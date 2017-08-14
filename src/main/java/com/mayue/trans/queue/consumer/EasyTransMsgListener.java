package com.mayue.trans.queue.consumer;

import com.mayue.trans.protocol.EasyTransRequest;


public interface EasyTransMsgListener {

	
    public EasyTransConsumeAction consume(EasyTransRequest<?, ?> message);
}