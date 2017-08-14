package com.mayue.trans.log.vo.compensable;

import com.mayue.trans.log.vo.DemiLeftContent;
import com.mayue.trans.protocol.EasyTransRequest;


public class PreCompensableCallContent extends DemiLeftContent {

	private static final long serialVersionUID = 1L;
	
	private EasyTransRequest<?,?> params;

	@Override
	public int getLogType() {
		return ContentType.PreCompensableCall.getContentTypeId();
	}

	public EasyTransRequest<?,?> getParams() {
		return params;
	}

	public void setParams(EasyTransRequest<?,?> params) {
		this.params = params;
	}
}
