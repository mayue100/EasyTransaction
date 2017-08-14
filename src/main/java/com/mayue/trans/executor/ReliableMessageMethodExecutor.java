package com.mayue.trans.executor;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.annotation.Resource;

import com.mayue.trans.EasyTransSynchronizer;
import com.mayue.trans.LogProcessor;
import com.mayue.trans.RemoteServiceCaller;
import com.mayue.trans.context.LogProcessContext;
import com.mayue.trans.context.event.DemiLogEventHandler;
import com.mayue.trans.log.vo.Content;
import com.mayue.trans.log.vo.msg.MessageRecordContent;
import com.mayue.trans.log.vo.msg.MessageSentContent;
import com.mayue.trans.protocol.BusinessIdentifer;
import com.mayue.trans.protocol.EasyTransRequest;
import com.mayue.trans.protocol.IdempotentTypeDeclare;
import com.mayue.trans.protocol.msg.PublishResult;
import com.mayue.trans.queue.producer.EasyTransMsgPublishResult;
import com.mayue.trans.serialization.ObjectSerializer;
import com.mayue.trans.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReliableMessageMethodExecutor implements EasyTransExecutor,LogProcessor,DemiLogEventHandler {

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
	
		MessageRecordContent content = new MessageRecordContent();
		content.setParams(params);
		transSynchronizer.registerLog(content);
		final PublishResult result = new PublishResult();
		result.setMessageContentId(content.getcId());
		
		FutureTask<PublishResult> future = new FutureTask<PublishResult>(new Callable<PublishResult>() {
			@Override
			public PublishResult call() throws Exception {
				return result;
			}
		});
		future.run();
		
		return (Future<R>) future;
	}

	@Override
	public boolean logProcess(LogProcessContext ctx, Content currentContent) {
		if(currentContent instanceof MessageRecordContent){
			MessageRecordContent msgRecordContent = (MessageRecordContent) currentContent;
			//register DemiLogEvent
			ctx.getDemiLogManager().registerSemiLogEventListener(msgRecordContent, this);
		}
		return true;
	}

	@Override
	public boolean onMatch(LogProcessContext logCtx, Content leftContent, Content rightContent) {
		return true;// do nothig
	}

	@Override
	public boolean onDismatch(LogProcessContext logCtx, Content leftContent) {
		if(logCtx.getFinalMasterTransStatus() == null){
			LOG.info("final trans status unknown,process later." + logCtx.getLogCollection());
			return false;//unknown,process later
		}else if(logCtx.getFinalMasterTransStatus()){
			//commit
			//get Log message
			MessageRecordContent content = (MessageRecordContent) leftContent;
			EasyTransRequest<?,?> msg = content.getParams();
			IdempotentTypeDeclare.TransactionId parentTrxId = logCtx.getTransactionId();
			//send message
			BusinessIdentifer businessIdentifer = ReflectUtil.getBusinessIdentifer(msg.getClass());
			EasyTransMsgPublishResult send = publisher.publish(businessIdentifer.appId(), businessIdentifer.busCode(), getMessageId(content, parentTrxId), msg,logCtx);
			//writeLog
			MessageSentContent messageSentContent = new MessageSentContent();
			messageSentContent.setLeftDemiConentId(leftContent.getcId());
			messageSentContent.setRemoteMessageId(send.getMessageId());
			logCtx.getLogCache().cacheLog(messageSentContent);
			LOG.info("Reliable message sent:" + businessIdentifer);
			return true;
		}else{
			//rollback
			//do nothing
			return true;
		}
	}

	private String getMessageId(MessageRecordContent content,
			IdempotentTypeDeclare.TransactionId parentTrxId) {
		return parentTrxId.getAppId()+"|"+parentTrxId.getBusCode()+"|"+parentTrxId.getTrxId()+"|"+content.getcId();
	}


}
