package com.mayue.trans.test.mockservice.wallet.easytrans;

import javax.annotation.Resource;

import com.mayue.trans.protocol.IdempotentTypeDeclare;
import com.mayue.trans.protocol.tcc.TccMethodRequest;
import org.springframework.stereotype.Component;

import com.mayue.trans.filter.EasyTransResult;
import com.mayue.trans.protocol.BusinessIdentifer;
import com.mayue.trans.protocol.tcc.TccMethod;
import com.mayue.trans.test.Constant;
import com.mayue.trans.test.mockservice.order.OrderService;
import com.mayue.trans.test.mockservice.wallet.WalletService;
import com.mayue.trans.test.mockservice.wallet.easytrans.WalletPayTccMethod.WalletPayTccMethodRequest;
import com.mayue.trans.test.mockservice.wallet.easytrans.WalletPayTccMethod.WalletPayTccMethodResult;

@Component
public class WalletPayTccMethod implements TccMethod<WalletPayTccMethodRequest, WalletPayTccMethodResult>{

	public static final String METHOD_NAME="pay";
	
	@Resource
	private WalletService wlletService;

	@Override
	public WalletPayTccMethodResult doTry(WalletPayTccMethodRequest param) {
		return wlletService.doTryPay(param);
	}

	@Override
	public void doConfirm(WalletPayTccMethodRequest param) {
		OrderService.checkThrowException(OrderService.EXCEPTION_TAG_IN_MIDDLE_OF_CONSISTENT_GUARDIAN_WITH_SUCCESS_MASTER_TRANS);
		wlletService.doConfirmPay(param);
	}


	@Override
	public void doCancel(WalletPayTccMethodRequest param) {
		wlletService.doCancelPay(param);
	}
	
	public static class WalletPayTccMethodResult extends EasyTransResult{
		private static final long serialVersionUID = 1L;
		private Long freezeAmount;
		public Long getFreezeAmount() {
			return freezeAmount;
		}
		public void setFreezeAmount(Long freezeAmount) {
			this.freezeAmount = freezeAmount;
		}
		
		@Override
		public String toString() {
			return "WalletPayTccMethodResult [freezeAmount=" + freezeAmount
					+ "]";
		}
	}
	
	@BusinessIdentifer(appId=Constant.APPID,busCode=METHOD_NAME,rpcTimeOut=2000)
	public static class WalletPayTccMethodRequest implements TccMethodRequest<WalletPayTccMethodResult> {

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
}
