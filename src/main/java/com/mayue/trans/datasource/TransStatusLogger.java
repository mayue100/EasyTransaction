package com.mayue.trans.datasource;


public interface TransStatusLogger {
	
	/**
	 * 检测主事务的状态
	 * @param appId
	 * @param trxId
	 * @return null for processing/unknown,false for roll back,true for committed  
	 */
	Boolean checkTransactionStatus(String appId,String busCode,String trxId);
	
	/**
	 * 业务事务commit之前，check 状态
	 * @param appId
	 * @param busCode
	 * @param trxId
	 */
	void writeExecuteFlag(String appId,String busCode,String trxId);
}
