package com.mayue.trans.datasource;

import javax.sql.DataSource;

import com.mayue.trans.protocol.EasyTransRequest;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * get the using JDBC-DataSource for specific 
 *
 */
public interface DataSourceSelector {
	
	/**
	 * for the use of master transaction
	 * @param appId
	 * @param busCode
	 * @param trxId
	 * @return
	 */
	DataSource selectDataSource(String appId,String busCode,String trxId);
	
	/**
	 * for the use of slave transaction
	 * @param appId
	 * @param busCode
	 * @param parentTrxId
	 * @return
	 */
	DataSource selectDataSource(String appId,String busCode,EasyTransRequest<?, ?> request);
	
	/**
	 * for the use of master transaction
	 * @param appId
	 * @param busCode
	 * @param trxId
	 * @return
	 */
	PlatformTransactionManager selectTransactionManager(String appId,String busCode,String trxId);
	
	/**
	 * for the use of master transaction
	 * @param appId
	 * @param busCode
	 * @param parentTrxId
	 * @return
	 */
	PlatformTransactionManager selectTransactionManager(String appId,String busCode,EasyTransRequest<?, ?> request);
}
