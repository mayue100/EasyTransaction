package com.mayue.trans.log.vo.aft;

import com.mayue.trans.log.vo.DemiRightContent;
import com.mayue.trans.log.vo.AfterCommit;

@AfterCommit
public class AfterTransCalledContent extends DemiRightContent {

	private static final long serialVersionUID = 1L;

	@Override
	public int getLogType() {
		return ContentType.AfterTransCalled.getContentTypeId();
	}
}
