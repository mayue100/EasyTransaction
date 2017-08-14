package com.mayue.trans.log.vo.compensable;

import com.mayue.trans.log.vo.AfterRollBack;
import com.mayue.trans.log.vo.DemiRightContent;

@AfterRollBack
public class CompensatedContent extends DemiRightContent {

	private static final long serialVersionUID = 1L;

	@Override
	public int getLogType() {
		return ContentType.Compensated.getContentTypeId();
	}
}
