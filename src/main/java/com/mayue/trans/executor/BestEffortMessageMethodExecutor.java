package com.mayue.trans.executor;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;

import com.mayue.trans.EasyTransSynchronizer;
import com.mayue.trans.RemoteServiceCaller;
import com.mayue.trans.context.LogProcessContext;
import com.mayue.trans.protocol.EasyTransRequest;
import com.mayue.trans.protocol.IdempotentTypeDeclare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mayue.trans.context.event.GuardianProcessEndEventHandler;
import com.mayue.trans.protocol.BusinessIdentifer;
import com.mayue.trans.protocol.msg.PublishResult;
import com.mayue.trans.serialization.ObjectSerializer;
import com.mayue.trans.util.ReflectUtil;

public class BestEffortMessageMethodExecutor implements EasyTransExecutor{

	@Resource
	private EasyTransSynchronizer transSynchronizer;
	
	@Resource
	private RemoteServiceCaller publisher;
	
	@Resource
	private ObjectSerializer serialization;
	
	private Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	@SuppressWarnings("unchecked")
	@Override
	public <P extends EasyTransRequest<R,E>,E extends EasyTransExecutor,R extends Serializable> Future<R> execute(final P params) {
		FutureTask<PublishResult> future = new FutureTask<PublishResult>(new Callable<PublishResult>() {
			@Override
			public PublishResult call() throws Exception {
				return new PublishResult();//do nothing
			}
		});
		future.run();
		
		
		final LogProcessContext logProcessContext = transSynchronizer.getLogProcessContext();
		
		//sent message after transaction commit
		logProcessContext.registerProcessEndEventListener(new GuardianProcessEndEventHandler() {
			@Override
			public boolean beforeProcessEnd(LogProcessContext logContext) {
				if(logProcessContext.getFinalMasterTransStatus()){
					BusinessIdentifer businessIdentifer = ReflectUtil.getBusinessIdentifer(params.getClass());
					String messageId = getMessageId("M" + logProcessContext.getAndIncTransUniqueId(), logContext.getTransactionId());
					publisher.publish(businessIdentifer.appId(), businessIdentifer.busCode(), messageId, params,logProcessContext);
					LOG.info("Best effort message sent." + messageId);
				}
				return true;
			}
		});
		
		return (Future<R>) future;
	}

	private String getMessageId(String innerId, IdempotentTypeDeclare.TransactionId parentTrxId) {
		return parentTrxId.getAppId()+"|"+parentTrxId.getBusCode()+"|"+parentTrxId.getTrxId()+"|"+innerId;
	}


}

