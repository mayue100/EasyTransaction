package com.mayue.trans.test.mockservice.express.easytrans;

import javax.annotation.Resource;

import com.mayue.trans.protocol.BusinessIdentifer;
import com.mayue.trans.protocol.IdempotentTypeDeclare;
import com.mayue.trans.protocol.aft.AfterMasterTransRequest;
import org.springframework.stereotype.Component;

import com.mayue.trans.filter.EasyTransResult;
import com.mayue.trans.protocol.aft.AfterMasterTransMethod;
import com.mayue.trans.test.Constant;
import com.mayue.trans.test.mockservice.express.ExpressService;
import com.mayue.trans.test.mockservice.express.easytrans.ExpressDeliverAfterTransMethod.AfterMasterTransMethodResult;
import com.mayue.trans.test.mockservice.express.easytrans.ExpressDeliverAfterTransMethod.ExpressDeliverAfterTransMethodRequest;

@Component
public class ExpressDeliverAfterTransMethod implements AfterMasterTransMethod<ExpressDeliverAfterTransMethodRequest, AfterMasterTransMethodResult>{
	
	public static final String BUSINESS_CODE = "noticeExpress";

	@Resource
	private ExpressService service;
	
	@Override
	public AfterMasterTransMethodResult afterTransaction(ExpressDeliverAfterTransMethodRequest param) {
		return service.afterTransaction(param);
	}
	
	@BusinessIdentifer(appId=Constant.APPID,busCode=BUSINESS_CODE)
	public static class ExpressDeliverAfterTransMethodRequest implements AfterMasterTransRequest<AfterMasterTransMethodResult> {

		private static final long serialVersionUID = 1L;
		
		private TransactionId parentTrxId;
		
		@Override
		public int getIdempotentType() {
			return IdempotentTypeDeclare.IDENPOTENT_TYPE_FRAMEWORK;
		}

		public TransactionId getParentTrxId() {
			return parentTrxId;
		}
		
		public void setParentTrxId(TransactionId parentTrxId) {
			this.parentTrxId = parentTrxId;
		}
		
		private Integer userId;
		
		private Long payAmount;

		public Long getPayAmount() {
			return payAmount;
		}

		public void setPayAmount(Long payAmount) {
			this.payAmount = payAmount;
		}

		public Integer getUserId() {
			return userId;
		}

		public void setUserId(Integer userId) {
			this.userId = userId;
		}
	}
	
	public static class AfterMasterTransMethodResult extends EasyTransResult{
		private static final long serialVersionUID = 1L;
		
		private String message;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
}
