package com.mayue.trans.context.event;

import java.util.ArrayList;
import java.util.List;

import com.mayue.trans.context.LogProcessContext;
import com.mayue.trans.protocol.IdempotentTypeDeclare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuardianProcessEndEventManager {

	private LogProcessContext logCtx;
	private Logger LOG = LoggerFactory.getLogger(this.getClass());
	private List<GuardianProcessEndEventHandler> listeners = new ArrayList<GuardianProcessEndEventHandler>(); 
	
	public GuardianProcessEndEventManager(LogProcessContext logCtx) {
		super();
		this.logCtx = logCtx;
	}

	
	public void registerSemiLogEventListener(GuardianProcessEndEventHandler handler){
		listeners.add(handler);
	}
	
	public boolean publish(){
		IdempotentTypeDeclare.TransactionId transactionId = logCtx.getTransactionId();
		for(GuardianProcessEndEventHandler handler:listeners){
			if(!handler.beforeProcessEnd(logCtx)){
				LOG.info("GuardianProcessEndEvent handler return false,appId:{},busCode,trxId:{}",transactionId.getAppId(),transactionId.getBusCode(),transactionId.getTrxId());
				return false;
			}
		}
		
		return true;
	}
}
