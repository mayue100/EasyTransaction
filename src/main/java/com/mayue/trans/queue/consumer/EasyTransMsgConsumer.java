package com.mayue.trans.queue.consumer;

import java.util.Collection;


public interface EasyTransMsgConsumer {

    /**
     * subscribe topic,override the previous subscription
     */
    void subscribe(String topic, Collection<String> tag,EasyTransMsgListener listener);
	
    String getConsumerId();
}