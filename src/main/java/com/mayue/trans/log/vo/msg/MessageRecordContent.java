package com.mayue.trans.log.vo.msg;

import com.mayue.trans.log.vo.DemiLeftContent;
import com.mayue.trans.protocol.EasyTransRequest;


public class MessageRecordContent extends DemiLeftContent {

	private static final long serialVersionUID = 1L;
	
	private EasyTransRequest<?,?> params;

	public EasyTransRequest<?,?> getParams() {
		return params;
	}

	public void setParams(EasyTransRequest<?,?> message) {
		this.params = message;
	}

	@Override
	public int getLogType() {
		return ContentType.MessageRecord.getContentTypeId();
	}
	
}
