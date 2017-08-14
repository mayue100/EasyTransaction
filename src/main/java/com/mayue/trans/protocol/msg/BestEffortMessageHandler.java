package com.mayue.trans.protocol.msg;

import com.mayue.trans.protocol.MessageBusinessProvider;

/**
 * 最大努力型消息服务接口
 * @param <P>
 */
public interface BestEffortMessageHandler<P extends BestEffortMessagePublishRequest>  extends MessageBusinessProvider<P> {
}
