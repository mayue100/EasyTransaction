package com.mayue.trans.test.mockservice.accounting;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.mayue.trans.test.Constant;
import com.mayue.trans.test.mockservice.TestUtil;
import com.mayue.trans.test.mockservice.accounting.easytrans.AccountingCpsMethod;
import com.mayue.trans.test.mockservice.accounting.easytrans.AccountingCpsMethod.AccountingRequest;
import com.mayue.trans.test.mockservice.accounting.easytrans.AccountingCpsMethod.AccountingResponse;

@Component
public class AccountingService {
	
	@Resource
	private TestUtil util;
	
	public int getTotalCost(int userId){
		JdbcTemplate jdbcTemplate = getJdbcTemplate(null);
		Integer queryForObject = jdbcTemplate.queryForObject("SELECT sum(amount) FROM `accounting` where user_id = ?;", Integer.class, userId);
		return queryForObject==null?0:queryForObject;
	}
	
	public AccountingResponse accounting(AccountingRequest param) {
		JdbcTemplate jdbcTemplate = getJdbcTemplate(param);
		
		int update = jdbcTemplate.update("INSERT INTO `accounting` (`accounting_id`, `p_app_id`, `p_bus_code`, `p_trx_id`, `user_id`, `amount`, `create_time`) VALUES (NULL, ?, ?, ?, ?, ?, ?);",
				param.getParentTrxId().getAppId(),
				param.getParentTrxId().getBusCode(),
				param.getParentTrxId().getTrxId(),
				param.getUserId(),
				param.getAmount(),
				new Date());
		
		if(update != 1){
			throw new RuntimeException("unkonw Exception!");
		}
		return new AccountingResponse();
	}

	public void reverseEntry(AccountingRequest param) {
		JdbcTemplate jdbcTemplate = getJdbcTemplate(param);
		
		int update = jdbcTemplate.update("INSERT INTO `accounting` (`accounting_id`, `p_app_id`, `p_bus_code`, `p_trx_id`, `user_id`, `amount`, `create_time`) VALUES (NULL, ?, ?, ?, ?, ?, ?);",
				param.getParentTrxId().getAppId(),
				param.getParentTrxId().getBusCode(),
				param.getParentTrxId().getTrxId(),
				param.getUserId(),
				-param.getAmount(),
				new Date());
		
		if(update != 1){
			throw new RuntimeException("unkonw Exception!");
		}
	}

	private JdbcTemplate getJdbcTemplate(AccountingRequest param) {
		return util.getJdbcTemplate(Constant.APPID,AccountingCpsMethod.METHOD_NAME,param);
	}

}
