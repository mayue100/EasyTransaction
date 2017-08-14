package com.mayue.trans.executor;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import com.mayue.trans.EasyTransSynchronizer;
import com.mayue.trans.LogProcessor;
import com.mayue.trans.RemoteServiceCaller;
import com.mayue.trans.config.EasyTransConifg;
import com.mayue.trans.context.LogProcessContext;
import com.mayue.trans.context.event.DemiLogEventHandler;
import com.mayue.trans.log.vo.Content;
import com.mayue.trans.log.vo.tcc.PreTccCallContent;
import com.mayue.trans.log.vo.tcc.TccCallCancelledContent;
import com.mayue.trans.log.vo.tcc.TccCallConfirmedContent;
import com.mayue.trans.protocol.BusinessIdentifer;
import com.mayue.trans.protocol.EasyTransRequest;
import com.mayue.trans.protocol.tcc.TccMethod;
import com.mayue.trans.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RelativeInterface(TccMethod.class)
public class TccMethodExecutor implements EasyTransExecutor,LogProcessor,DemiLogEventHandler {

	@Resource
	private EasyTransSynchronizer transSynchronizer;
	
	@Resource
	private RemoteServiceCaller rpcClient;
	
	@Resource
	private EasyTransConifg config;
	
	private Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	private static final String TRY_METHOD_NAME = "doTry";
	private static final String CONFIRM_METHOD_NAME = "doConfirm";
	private static final String CANCEL_METHOD_NAME = "doCancel";
	
	@Override
	public <P extends EasyTransRequest<R,E>,E extends EasyTransExecutor,R extends Serializable> Future<R> execute(final P params) {
		final LogProcessContext logProcessContext = transSynchronizer.getLogProcessContext();
		Callable<R> callable = new Callable<R>() {
			@Override
			public R call() throws Exception {
				BusinessIdentifer businessIdentifer = ReflectUtil.getBusinessIdentifer(params.getClass());
				return (R) rpcClient.call(businessIdentifer.appId(), businessIdentifer.busCode(), TRY_METHOD_NAME, params,logProcessContext);
			}
		};
		
		PreTccCallContent content = new PreTccCallContent();
		content.setParams(params);
		return transSynchronizer.executeMethod(callable, content);
	}

	@Override
	public boolean logProcess(LogProcessContext ctx, Content currentContent) {
		if(currentContent instanceof PreTccCallContent){
			PreTccCallContent preCallContent = (PreTccCallContent) currentContent;
			//register DemiLogEvent
			ctx.getDemiLogManager().registerSemiLogEventListener(preCallContent, this);
		}
		return true;
	}

	@Override
	public boolean onMatch(LogProcessContext logCtx, Content leftContent, Content rightContent) {
		return true;// do nothig
	}

	@Override
	public boolean onDismatch(LogProcessContext logCtx, Content leftContent) {
		PreTccCallContent preCallContent = (PreTccCallContent) leftContent;
		EasyTransRequest<?,?> params = preCallContent.getParams();
		BusinessIdentifer businessIdentifer = ReflectUtil.getBusinessIdentifer(params.getClass());
		if(logCtx.getFinalMasterTransStatus() == null){
			LOG.info("final trans status unknown,process later." + logCtx.getLogCollection());
			return false;//unknown,process later
		}else if(logCtx.getFinalMasterTransStatus()){
			//commit
			//execute confirm and then write Log
			rpcClient.callWithNoReturn(businessIdentifer.appId(), businessIdentifer.busCode(), CONFIRM_METHOD_NAME, preCallContent.getParams(),logCtx);
			TccCallConfirmedContent tccCallConfirmedContent = new TccCallConfirmedContent();
			tccCallConfirmedContent.setLeftDemiConentId(leftContent.getcId());
			logCtx.getLogCache().cacheLog(tccCallConfirmedContent);
			return true;
		}else{
			//roll back
			//execute cancel and then write Log
			rpcClient.callWithNoReturn(businessIdentifer.appId(), businessIdentifer.busCode(), CANCEL_METHOD_NAME, preCallContent.getParams(),logCtx);
			TccCallCancelledContent tccCallCanceledContent = new TccCallCancelledContent();
			tccCallCanceledContent.setLeftDemiConentId(leftContent.getcId());
			return true;
		}
	}


}
