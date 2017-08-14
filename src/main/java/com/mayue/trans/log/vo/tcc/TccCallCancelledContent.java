package com.mayue.trans.log.vo.tcc;

import com.mayue.trans.log.vo.DemiRightContent;
import com.mayue.trans.log.vo.AfterRollBack;

@AfterRollBack
public class TccCallCancelledContent extends DemiRightContent {

	private static final long serialVersionUID = 1L;


	@Override
	public int getLogType() {
		return ContentType.TccCallCanceled.getContentTypeId();
	}
}
