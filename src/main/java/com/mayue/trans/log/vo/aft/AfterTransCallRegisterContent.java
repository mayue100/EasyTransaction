package com.mayue.trans.log.vo.aft;

import com.mayue.trans.log.vo.DemiLeftContent;
import com.mayue.trans.protocol.EasyTransRequest;


public class AfterTransCallRegisterContent extends DemiLeftContent {

	private static final long serialVersionUID = 1L;
	
	private EasyTransRequest<?,?> params;

	@Override
	public int getLogType() {
		return ContentType.AfterTransCallRegister.getContentTypeId();
	}

	public EasyTransRequest<?,?> getParams() {
		return params;
	}

	public void setParams(EasyTransRequest<?,?> params) {
		this.params = params;
	}
}
