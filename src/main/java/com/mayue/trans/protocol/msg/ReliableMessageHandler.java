package com.mayue.trans.protocol.msg;

import com.mayue.trans.protocol.MessageBusinessProvider;


public interface ReliableMessageHandler<P extends ReliableMessagePublishRequest> extends MessageBusinessProvider<P> {
}
