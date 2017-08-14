package com.mayue.trans.test.mockservice.point;

import javax.annotation.Resource;

import com.mayue.trans.config.EasyTransConifg;
import com.mayue.trans.protocol.BusinessIdentifer;
import com.mayue.trans.test.mockservice.TestUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.mayue.trans.test.mockservice.order.OrderMessage;
import com.mayue.trans.util.ReflectUtil;

@Component
public class PointService {
	
	
	@Resource
	private TestUtil util;
	
	@Resource
	private EasyTransConifg config;
	
	public void addPointForBuying(OrderMessage msg){

		BusinessIdentifer businessIdentifer = ReflectUtil.getBusinessIdentifer(msg.getClass());
		
		JdbcTemplate jdbcTemplate = util.getJdbcTemplate(config.getAppId(),businessIdentifer.busCode(),msg);
		int update = jdbcTemplate.update("update `point` set point = point + ? where user_id = ?;", 
				msg.getAmount(),msg.getUserId());
		
		if(update != 1){
			throw new RuntimeException("can not find specific user id!");
		}
	}
	
	
	public int getUserPoint(int userId){
		JdbcTemplate jdbcTemplate = util.getJdbcTemplate(config.getAppId(),OrderMessage.BUSINESS_CODE,(OrderMessage)null);
		Integer queryForObject = jdbcTemplate.queryForObject("select point from point where user_id = ?", Integer.class, userId);
		return queryForObject == null?0:queryForObject;
	}
}
