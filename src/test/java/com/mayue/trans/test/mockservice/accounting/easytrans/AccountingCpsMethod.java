package com.mayue.trans.test.mockservice.accounting.easytrans;

import java.io.Serializable;

import javax.annotation.Resource;

import com.mayue.trans.protocol.BusinessIdentifer;
import com.mayue.trans.protocol.IdempotentTypeDeclare;
import com.mayue.trans.protocol.cps.CompensableMethod;
import com.mayue.trans.test.mockservice.accounting.AccountingService;
import org.springframework.stereotype.Component;

import com.mayue.trans.protocol.cps.CompensableMethodRequest;
import com.mayue.trans.test.Constant;
import com.mayue.trans.test.mockservice.accounting.easytrans.AccountingCpsMethod.AccountingRequest;
import com.mayue.trans.test.mockservice.accounting.easytrans.AccountingCpsMethod.AccountingResponse;
import com.mayue.trans.test.mockservice.order.OrderService;

@Component
public class AccountingCpsMethod implements CompensableMethod<AccountingRequest, AccountingResponse> {


    @Resource
    private AccountingService service;

    public static final String METHOD_NAME = "accounting";

    @Override
    public AccountingResponse doCompensableBusiness(AccountingRequest param) {
        return service.accounting(param);
    }

    @Override
    public void compensation(AccountingRequest param) {
        OrderService.checkThrowException(OrderService.EXCEPTION_TAG_IN_MIDDLE_OF_CONSISTENT_GUARDIAN_WITH_ROLLEDBACK_MASTER_TRANS);
        service.reverseEntry(param);
    }

    @BusinessIdentifer(appId = Constant.APPID, busCode = METHOD_NAME)
    public static class AccountingRequest implements CompensableMethodRequest<AccountingResponse> {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private TransactionId parentTrxId;

        private Integer userId;

        private Long amount;

        @Override
        public int getIdempotentType() {
            return IdempotentTypeDeclare.IDENPOTENT_TYPE_FRAMEWORK;
        }

        @Override
        public TransactionId getParentTrxId() {
            return parentTrxId;
        }

        public void setParentTrxId(TransactionId parentTrxId) {
            this.parentTrxId = parentTrxId;
        }

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

    public static class AccountingResponse implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
    }


}
