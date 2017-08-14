package com.mayue.trans.test.mockservice.wallet;

import javax.annotation.Resource;

import com.mayue.trans.test.mockservice.TestUtil;
import com.mayue.trans.test.mockservice.wallet.easytrans.WalletPayTccMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.mayue.trans.test.Constant;

@Component
public class WalletService {
	
	@Resource
	private TestUtil util;
	
	public int getUserTotalAmount(int userId){
		JdbcTemplate jdbcTemplate = util.getJdbcTemplate(Constant.APPID, WalletPayTccMethod.METHOD_NAME,(WalletPayTccMethod.WalletPayTccMethodRequest)null);
		Integer queryForObject = jdbcTemplate.queryForObject("select total_amount from wallet where user_id = ?", Integer.class, userId);
		
		return queryForObject == null?0:queryForObject;
	}
	
	public int getUserFreezeAmount(int userId){
		JdbcTemplate jdbcTemplate = util.getJdbcTemplate(Constant.APPID,WalletPayTccMethod.METHOD_NAME,(WalletPayTccMethod.WalletPayTccMethodRequest)null);
		Integer queryForObject = jdbcTemplate.queryForObject("select freeze_amount from wallet where user_id = ?", Integer.class, userId);
		
		return queryForObject == null?0:queryForObject;
	}
	
	public WalletPayTccMethod.WalletPayTccMethodResult doTryPay(WalletPayTccMethod.WalletPayTccMethodRequest param) {
		JdbcTemplate jdbcTemplate = util.getJdbcTemplate(Constant.APPID,WalletPayTccMethod.METHOD_NAME,param);
		int update = jdbcTemplate.update("update `wallet` set freeze_amount = freeze_amount + ? where user_id = ? and (total_amount - freeze_amount) >= ?;", 
				param.getPayAmount(),param.getUserId(),param.getPayAmount());
		
		if(update != 1){
			throw new RuntimeException("can not find specific user id or have not enought money");
		}
		
		WalletPayTccMethod.WalletPayTccMethodResult walletPayTccMethodResult = new WalletPayTccMethod.WalletPayTccMethodResult();
		walletPayTccMethodResult.setFreezeAmount(param.getPayAmount());
		return walletPayTccMethodResult;
	}
	

	public void doConfirmPay(WalletPayTccMethod.WalletPayTccMethodRequest param) {
		JdbcTemplate jdbcTemplate = util.getJdbcTemplate(Constant.APPID,WalletPayTccMethod.METHOD_NAME,param);
		int update = jdbcTemplate.update("update `wallet` set freeze_amount = freeze_amount - ?, total_amount = total_amount - ? where user_id = ?;", 
				param.getPayAmount(),param.getPayAmount(),param.getUserId());
		
		if(update != 1){
			throw new RuntimeException("unknow Exception!");
		}
	}
	
	public void doCancelPay(WalletPayTccMethod.WalletPayTccMethodRequest param) {
		JdbcTemplate jdbcTemplate = util.getJdbcTemplate(Constant.APPID,WalletPayTccMethod.METHOD_NAME,param);
		int update = jdbcTemplate.update("update `wallet` set freeze_amount = freeze_amount - ? where user_id = ?;", 
				param.getPayAmount(),param.getUserId());
		if(update != 1){
			throw new RuntimeException("unknow Exception!");
		}
	}
	
}
