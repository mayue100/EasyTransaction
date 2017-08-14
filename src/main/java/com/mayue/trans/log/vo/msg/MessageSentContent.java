package com.mayue.trans.log.vo.msg;

import com.mayue.trans.log.vo.AfterCommit;
import com.mayue.trans.log.vo.DemiRightContent;

@AfterCommit
public class MessageSentContent extends DemiRightContent {

	private static final long serialVersionUID = 1L;
	
	private String remoteMessageId;
	
	@Override
	public int getLogType() {
		return ContentType.MessageSent.getContentTypeId();
	}

	public String getRemoteMessageId() {
		return remoteMessageId;
	}

	public void setRemoteMessageId(String remoteMessageId) {
		this.remoteMessageId = remoteMessageId;
	}
}
