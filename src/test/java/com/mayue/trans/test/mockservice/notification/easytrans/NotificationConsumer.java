package com.mayue.trans.test.mockservice.notification.easytrans;

import javax.annotation.Resource;

import com.mayue.trans.protocol.EasyTransRequest;
import com.mayue.trans.queue.consumer.EasyTransConsumeAction;
import com.mayue.trans.test.mockservice.notification.NotificationService;
import com.mayue.trans.test.mockservice.order.NotReliableOrderMessage;
import org.springframework.stereotype.Component;

import com.mayue.trans.protocol.msg.BestEffortMessageHandler;

@Component
public class NotificationConsumer implements BestEffortMessageHandler<NotReliableOrderMessage> {

	@Resource
	private NotificationService service;
	
	@Override
	public EasyTransConsumeAction consume(EasyTransRequest<?, ?> request) {
		service.addPointForBuying((NotReliableOrderMessage) request);
		return EasyTransConsumeAction.CommitMessage;
	}
	
}
