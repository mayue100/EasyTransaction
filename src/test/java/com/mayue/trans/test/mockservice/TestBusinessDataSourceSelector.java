package com.mayue.trans.test.mockservice;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.mayue.trans.protocol.EasyTransRequest;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;

import com.mayue.trans.datasource.DataSourceSelector;

public class TestBusinessDataSourceSelector implements DataSourceSelector {

	@Resource
	private ApplicationContext ctx;
	
	@Override
	public DataSource selectDataSource(String appId, String busCode, String trxId) {
		if(appId != null){
			return ctx.getBean(busCode, DataSource.class);
		}else{
			return ctx.getBean("whole", DataSource.class);
		}
	}

	@Override
	public DataSource selectDataSource(String appId, String busCode,
			EasyTransRequest<?, ?> request) {
		if(appId != null){
			return ctx.getBean(busCode, DataSource.class);
		}else{
			return ctx.getBean("whole", DataSource.class);
		}
	}

	@Override
	public PlatformTransactionManager selectTransactionManager(String appId, String busCode, String trxId) {
		if(appId != null){
			return ctx.getBean(busCode+"TransactionManager", PlatformTransactionManager.class);
		}else{
			return ctx.getBean("wholeTransactionManager", PlatformTransactionManager.class);
		}
	}

	@Override
	public PlatformTransactionManager selectTransactionManager(String appId, String busCode, EasyTransRequest<?, ?> request) {
		if(appId != null){
			return ctx.getBean(busCode+"TransactionManager", PlatformTransactionManager.class);
		}else{
			return ctx.getBean("wholeTransactionManager", PlatformTransactionManager.class);
		}
	}

}
