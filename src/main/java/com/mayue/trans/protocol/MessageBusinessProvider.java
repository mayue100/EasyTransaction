package com.mayue.trans.protocol;

import com.mayue.trans.queue.consumer.EasyTransConsumeAction;

/**
 * 消息服务提供者
 * @param <P>
 */
public interface MessageBusinessProvider<P extends EasyTransRequest<?, ?>> extends BusinessProvider<P> {
	  public EasyTransConsumeAction consume(EasyTransRequest<?, ?> request);
}
