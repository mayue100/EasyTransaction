package com.mayue.trans.log.vo.trx;

import com.mayue.trans.log.vo.DemiRightContent;


public class TransactionFinishedContent extends DemiRightContent {

	private static final long serialVersionUID = 1L;

	@Override
	public int getLogType() {
		return ContentType.TransactionFininshed.getContentTypeId();
	}
}
