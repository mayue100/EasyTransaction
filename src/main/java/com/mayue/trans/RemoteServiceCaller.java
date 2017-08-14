package com.mayue.trans;

import java.io.Serializable;

import javax.annotation.Resource;

import com.mayue.trans.context.LogProcessContext;
import com.mayue.trans.protocol.EasyTransRequest;
import com.mayue.trans.queue.producer.EasyTransMsgPublishResult;
import com.mayue.trans.rpc.EasyTransRpcConsumer;
import com.mayue.trans.queue.producer.EasyTransMsgPublisher;
import com.mayue.trans.serialization.ObjectSerializer;

public class RemoteServiceCaller {
	
	@Resource
	private EasyTransRpcConsumer consumer;
	
	@Resource
	private EasyTransMsgPublisher publisher;
	
	@Resource
	private ObjectSerializer serializer;
	
	public <P extends EasyTransRequest<R,?>,R extends Serializable> R call(String appId, String busCode, String innerMethod, P params, LogProcessContext logContext){
		initEasyTransRequest(params,logContext);
		return consumer.call(appId, busCode, innerMethod, params);
	}
	
	public <P extends EasyTransRequest<R,?>,R extends Serializable> void callWithNoReturn(String appId,String busCode,String innerMethod,P params,LogProcessContext logContext){
		initEasyTransRequest(params,logContext);
		consumer.callWithNoReturn(appId, busCode, innerMethod, params);
	}
	
	
	public EasyTransMsgPublishResult publish(String topic, String tag, String key, EasyTransRequest<?, ?> request, LogProcessContext logContext){
		initEasyTransRequest(request,logContext);
		return publisher.publish(topic, tag, key, serializer.serialization(request));
	}
	
	private void initEasyTransRequest(EasyTransRequest<?, ?> request,LogProcessContext logContext){
		if(request.getParentTrxId() == null){
			request.setParentTrxId(logContext.getTransactionId());
		}
	}
}
