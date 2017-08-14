package com.mayue.trans.test.mockservice.order;

import com.mayue.trans.protocol.BusinessIdentifer;
import com.mayue.trans.protocol.IdempotentTypeDeclare;
import com.mayue.trans.protocol.msg.ReliableMessagePublishRequest;
import com.mayue.trans.test.Constant;

@BusinessIdentifer(appId= Constant.APPID,busCode=OrderMessage.BUSINESS_CODE)
public class OrderMessage implements ReliableMessagePublishRequest {

	public static final String BUSINESS_CODE = "ReliableOrderMsg";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private IdempotentTypeDeclare.TransactionId parentTrxId;
	
	@Override
	public int getIdempotentType() {
		return IdempotentTypeDeclare.IDENPOTENT_TYPE_FRAMEWORK;
	}

	public IdempotentTypeDeclare.TransactionId getParentTrxId() {
		return parentTrxId;
	}

	public void setParentTrxId(IdempotentTypeDeclare.TransactionId parentTrxId) {
		this.parentTrxId = parentTrxId;
	}
	
	
	
	private Integer userId;
	private Long amount;
	
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}
}
