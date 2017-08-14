package com.mayue.trans.log.vo.tcc;

import com.mayue.trans.log.vo.DemiLeftContent;
import com.mayue.trans.protocol.EasyTransRequest;


public class PreTccCallContent extends DemiLeftContent {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 调用的参数
	 */
	private EasyTransRequest<?,?> params;

	@Override
	public int getLogType() {
		return ContentType.PreTccCall.getContentTypeId();
	}

	public EasyTransRequest<?,?> getParams() {
		return params;
	}

	public void setParams(EasyTransRequest<?,?> params) {
		this.params = params;
	}
}
