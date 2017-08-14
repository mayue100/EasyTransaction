package com.mayue.trans.test.mockservice;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.mayue.trans.protocol.EasyTransRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.mayue.trans.datasource.DataSourceSelector;

@Component
public class TestUtil {
	@Resource
	private DataSourceSelector selector;
	
	public JdbcTemplate getJdbcTemplate(String appId,String methodName,EasyTransRequest<?, ?> param) {
		DataSource selectDataSource = selector.selectDataSource(appId, methodName, param);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(selectDataSource);//for test,in real projects,should be cache
		return jdbcTemplate;
	}
	
	
	public JdbcTemplate getJdbcTemplate(String appId,String methodName,String trxId) {
		DataSource selectDataSource = selector.selectDataSource(appId, methodName, trxId);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(selectDataSource);//for test,in real projects,should be cache
		return jdbcTemplate;
	}
	
	private Set<String> exceptionPosition = new HashSet<String>();
	
	void clearException(){
		exceptionPosition.clear();
	}
	
	void setException(String tag){
		exceptionPosition.add(tag);
	}
	
	void checkExcetpionThrow(String tag){
		if(exceptionPosition.contains(tag)){
			throw new RuntimeException("Designed Exception:" + tag);
		}
	}
	
}
