package com.mayue.trans.test.mockservice.express;

import javax.annotation.Resource;

import com.mayue.trans.test.Constant;
import com.mayue.trans.test.mockservice.TestUtil;
import com.mayue.trans.test.mockservice.express.easytrans.ExpressDeliverAfterTransMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ExpressService {
	
	@Resource
	private TestUtil util;
	
	public ExpressDeliverAfterTransMethod.AfterMasterTransMethodResult afterTransaction(ExpressDeliverAfterTransMethod.ExpressDeliverAfterTransMethodRequest param) {
		ExpressDeliverAfterTransMethod.AfterMasterTransMethodResult afterMasterTransMethodResult = new ExpressDeliverAfterTransMethod.AfterMasterTransMethodResult();
		afterMasterTransMethodResult.setMessage(callExternalServiceForPickupCargo(param));
		return afterMasterTransMethodResult;
	}

	public int getUserExpressCount(int userId){
		
		JdbcTemplate jdbcTemplate = util.getJdbcTemplate(Constant.APPID,ExpressDeliverAfterTransMethod.BUSINESS_CODE,(ExpressDeliverAfterTransMethod.ExpressDeliverAfterTransMethodRequest)null);
		Integer queryForObject = jdbcTemplate.queryForObject("select count(1) from express where user_id = ?", Integer.class,userId);
		if(queryForObject == null){
			queryForObject = 0;
		}
		return queryForObject;
	}

	private String callExternalServiceForPickupCargo(ExpressDeliverAfterTransMethod.ExpressDeliverAfterTransMethodRequest param) {
		//pretend to be a external service
		JdbcTemplate jdbcTemplate = util.getJdbcTemplate(Constant.APPID,ExpressDeliverAfterTransMethod.BUSINESS_CODE,param);
		int update = jdbcTemplate.update("INSERT INTO `express` (`p_app_id`, `p_bus_code`, `p_trx_id`, `user_id`) VALUES (?, ?, ?, ?);", 
				param.getParentTrxId().getAppId(),param.getParentTrxId().getBusCode(),param.getParentTrxId().getTrxId(),param.getUserId());
		
		if(update != 1){
			throw new RuntimeException("unkonwn Exception!");
		}
		
		return "your cargo will be depart in 10 minutes ";
	}
	
	
}
