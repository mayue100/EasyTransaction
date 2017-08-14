package com.mayue.trans.idempotent.exception;

import com.mayue.trans.filter.EasyTransResult;

public class ResultWrapperExeception extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private EasyTransResult result;
	
	public ResultWrapperExeception(EasyTransResult result){
		this.result = result;
	}

	public EasyTransResult getResult() {
		return result;
	}
}
