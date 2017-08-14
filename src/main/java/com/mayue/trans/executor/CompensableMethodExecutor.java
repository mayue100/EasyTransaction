package com.mayue.trans.executor;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import com.mayue.trans.EasyTransSynchronizer;
import com.mayue.trans.RemoteServiceCaller;
import com.mayue.trans.context.LogProcessContext;
import com.mayue.trans.context.event.DemiLogEventHandler;
import com.mayue.trans.log.vo.Content;
import com.mayue.trans.log.vo.compensable.PreCompensableCallContent;
import com.mayue.trans.protocol.EasyTransRequest;
import com.mayue.trans.protocol.cps.CompensableMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mayue.trans.LogProcessor;
import com.mayue.trans.config.EasyTransConifg;
import com.mayue.trans.log.vo.compensable.CompensatedContent;
import com.mayue.trans.protocol.BusinessIdentifer;
import com.mayue.trans.util.ReflectUtil;

@RelativeInterface(CompensableMethod.class)
public class CompensableMethodExecutor implements EasyTransExecutor,LogProcessor,DemiLogEventHandler {

	@Resource
	private EasyTransSynchronizer transSynchronizer;
	
	@Resource
	private RemoteServiceCaller rpcClient;
	
	@Resource
	private EasyTransConifg config;
	
	private Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	private static final String COMPENSABLE_BUSINESS_METHOD_NAME = "doCompensableBusiness";
	private static final String COMPENSATION_METHOD_NAME = "compensation";
	
	@Override
	public <P extends EasyTransRequest<R,E>,E extends EasyTransExecutor,R extends Serializable> Future<R> execute(final P params) {
		final LogProcessContext logProcessContext = transSynchronizer.getLogProcessContext();
		Callable<R> callable = new Callable<R>() {
			@Override
			public R call() throws Exception {
				BusinessIdentifer businessIdentifer = ReflectUtil.getBusinessIdentifer(params.getClass());
				return (R) rpcClient.call(businessIdentifer.appId(), businessIdentifer.busCode(), COMPENSABLE_BUSINESS_METHOD_NAME, params,logProcessContext);
			}
		};
		
		PreCompensableCallContent content = new PreCompensableCallContent();
		content.setParams(params);
		
		return transSynchronizer.executeMethod(callable, content);
	}

	@Override
	public boolean logProcess(LogProcessContext ctx, Content currentContent) {
		if(currentContent instanceof PreCompensableCallContent){
			PreCompensableCallContent preCallContent = (PreCompensableCallContent) currentContent;
			//register DemiLogEvent
			ctx.getDemiLogManager().registerSemiLogEventListener(preCallContent, this);
		}
		return true;
	}

	@Override
	public boolean onMatch(LogProcessContext logCtx, Content leftContent, Content rightContent) {
		return true;// do nothing
	}

	@Override
	public boolean onDismatch(LogProcessContext logCtx, Content leftContent) {
		PreCompensableCallContent preCpsContent = (PreCompensableCallContent) leftContent;
		EasyTransRequest<?,?> params = preCpsContent.getParams();
		BusinessIdentifer businessIdentifer = ReflectUtil.getBusinessIdentifer(params.getClass());
		
		if(logCtx.getFinalMasterTransStatus() == null){
			LOG.info("final trans status unknown,process later." + logCtx.getLogCollection());
			return false;
		}else if(logCtx.getFinalMasterTransStatus()){
			//commit
			//do nothing
			return true;
		}else{
			//roll back
			//execute compensation and then write Log
			rpcClient.callWithNoReturn(businessIdentifer.appId(), businessIdentifer.busCode(), COMPENSATION_METHOD_NAME, preCpsContent.getParams(),logCtx);
			CompensatedContent compensatedContent = new CompensatedContent();
			compensatedContent.setLeftDemiConentId(leftContent.getcId());
			LOG.info("Compensable method executed:" + businessIdentifer);
			return true;
		}
	}


}
