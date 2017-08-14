package com.mayue.trans.test.mockservice.point.easytrans;

import javax.annotation.Resource;

import com.mayue.trans.protocol.EasyTransRequest;
import org.springframework.stereotype.Component;

import com.mayue.trans.protocol.msg.ReliableMessageHandler;
import com.mayue.trans.queue.consumer.EasyTransConsumeAction;
import com.mayue.trans.test.mockservice.order.OrderMessage;
import com.mayue.trans.test.mockservice.point.PointService;

@Component
public class PointOrderSuccessConsumer implements ReliableMessageHandler<OrderMessage> {

	@Resource
	private PointService pointService;
	
	@Override
	public EasyTransConsumeAction consume(EasyTransRequest<?, ?> request) {
		pointService.addPointForBuying((OrderMessage) request);
		return EasyTransConsumeAction.CommitMessage;
	}

}
