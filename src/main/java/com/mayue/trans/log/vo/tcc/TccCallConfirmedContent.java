package com.mayue.trans.log.vo.tcc;

import com.mayue.trans.log.vo.AfterCommit;
import com.mayue.trans.log.vo.DemiRightContent;

@AfterCommit
public class TccCallConfirmedContent extends DemiRightContent {

	private static final long serialVersionUID = 1L;

	@Override
	public int getLogType() {
		return ContentType.TccCallConfirmed.getContentTypeId();
	}
}
