package com.mayue.trans.test.mockservice.notification;

import com.mayue.trans.test.mockservice.order.NotReliableOrderMessage;
import org.springframework.stereotype.Component;

@Component
public class NotificationService {

	public void addPointForBuying(NotReliableOrderMessage msg){
		System.out.println(String.format("user:%s used:%s", msg.getUserId(),msg.getAmount()));
	}
	
}
