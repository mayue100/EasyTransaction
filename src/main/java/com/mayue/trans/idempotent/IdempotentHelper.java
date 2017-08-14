package com.mayue.trans.idempotent;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.mayue.trans.protocol.EasyTransRequest;
import com.mayue.trans.provider.factory.ListableProviderFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import com.mayue.trans.datasource.DataSourceSelector;
import com.mayue.trans.filter.EasyTransFilterChain;
import com.mayue.trans.protocol.BusinessIdentifer;
import com.mayue.trans.protocol.ExecuteOrder;
import com.mayue.trans.serialization.ObjectSerializer;
import com.mayue.trans.util.ReflectUtil;

public class IdempotentHelper {
	
	@Resource
	private DataSourceSelector selector;
	
	@Resource
	private ObjectSerializer serializer;
	
	@Resource
	private ListableProviderFactory providerFactory;
	
	private static final String TRANSACTION_MANAGER = "TRANSACTION_MANAGER";
	private static final String DATA_SOURCE = "DATA_SOURCE";
	private static final String JDBC_TEMPLATE = "JDBC_TEMPLATE";
	
	public PlatformTransactionManager getTransactionManager(EasyTransFilterChain filterChain, EasyTransRequest<?, ?> reqest){
		PlatformTransactionManager transactionManager = filterChain.getResource(TRANSACTION_MANAGER);
		if(transactionManager == null){
			transactionManager = selector.selectTransactionManager(filterChain.getAppId(), filterChain.getBusCode(),reqest);
			filterChain.bindResource(TRANSACTION_MANAGER, transactionManager);
		}
		
		return transactionManager;
	}
	
	public DataSource getDatasource(EasyTransFilterChain filterChain, EasyTransRequest<?, ?> reqest){
		DataSource dataSource = filterChain.getResource(DATA_SOURCE);
		if(dataSource == null){
			dataSource = selector.selectDataSource(filterChain.getAppId(), filterChain.getBusCode(),reqest);
			filterChain.bindResource(DATA_SOURCE, dataSource);
		}
		return dataSource;
	}
	
	public JdbcTemplate getJdbcTemplate(EasyTransFilterChain filterChain, EasyTransRequest<?, ?> reqest){
		JdbcTemplate jdbcTemplate = filterChain.getResource(JDBC_TEMPLATE);
		if(jdbcTemplate == null){
			DataSource datasource = getDatasource(filterChain, reqest);
			jdbcTemplate = new JdbcTemplate(datasource);
			filterChain.bindResource(JDBC_TEMPLATE, jdbcTemplate);
		}
		return jdbcTemplate;
	}

	private BeanPropertyRowMapper<IdempotentPo> beanPropertyRowMapper = new BeanPropertyRowMapper<IdempotentPo>(IdempotentPo.class);
	/**
	 * get execute result from database
	 * @param filterChain
	 * @param reqest
	 * @return
	 */
	public IdempotentPo getIdempotentPo(EasyTransFilterChain filterChain, EasyTransRequest<?, ?> reqest){
		BusinessIdentifer businessType = ReflectUtil.getBusinessIdentifer(reqest.getClass());
		JdbcTemplate jdbcTemplate = getJdbcTemplate(filterChain, reqest);
		 List<IdempotentPo> listQuery = jdbcTemplate.query(
				"select * from idempotent where src_app_id = ? and src_bus_code = ? and src_trx_id = ? and app_id = ? and bus_code = ? ", 
				new Object[]{
						reqest.getParentTrxId().getAppId(),
						reqest.getParentTrxId().getBusCode(),
						reqest.getParentTrxId().getTrxId(),
						businessType.appId(),
						businessType.busCode()},
				beanPropertyRowMapper
				);
		 
		 if(listQuery.size() == 1){
			 return listQuery.get(0);
		 }else if (listQuery.size() == 0){
			 return null;
		 }else{
			 throw new RuntimeException("Unkonw Error!" + listQuery);
		 }
	}

	
	private ConcurrentHashMap<String, Object> mapExecuteOrder = new ConcurrentHashMap<String, Object>();
	private static final Object NULL_OBJECT = new Object();
	public ExecuteOrder getExecuteOrder(String appId, String busCode ,String innerMethod) {
		
		if(EasyTransFilterChain.MESSAGE_BUSINESS_FLAG.equals(innerMethod)){
			return null;
		}
		
		String key = getKey(appId, busCode, innerMethod);
		Object object = mapExecuteOrder.get(key);
		if(object == null){
			Class<?> serviceInterface = providerFactory.getServiceInterface(appId, busCode);
			Method[] methods = serviceInterface.getMethods();
			for(Method method:methods){
				if(method.getName().equals(innerMethod)){
					ExecuteOrder annotation = method.getAnnotation(ExecuteOrder.class);
					if(annotation == null){
						object = NULL_OBJECT;
					}else{
						object = annotation;
					}
					mapExecuteOrder.put(key, object);
					break;
				}
			}
		}
		
		if(object == NULL_OBJECT){
			return null;
		}else{
			return (ExecuteOrder) object;
		}
	}

	private String getKey(String appId, String busCode ,String innerMethod){
		return appId + busCode + innerMethod;
	}

	public static class IdempotentPo{
		private String srcAppId;
		private String srcBusCode;
		private String srcTrxId;
		private String appId;
		private String busCode;
		private String calledMethods;
		private String md5;
		private byte[] syncMethodResult;
		private Date createTime;
		private Date updateTime;
		private Integer lockVersion;
		public String getSrcAppId() {
			return srcAppId;
		}
		public void setSrcAppId(String srcAppId) {
			this.srcAppId = srcAppId;
		}
		public String getSrcBusCode() {
			return srcBusCode;
		}
		public void setSrcBusCode(String srcBusCode) {
			this.srcBusCode = srcBusCode;
		}
		public String getSrcTrxId() {
			return srcTrxId;
		}
		public void setSrcTrxId(String srcTrxId) {
			this.srcTrxId = srcTrxId;
		}
		public String getAppId() {
			return appId;
		}
		public void setAppId(String appId) {
			this.appId = appId;
		}
		public String getBusCode() {
			return busCode;
		}
		public void setBusCode(String busCode) {
			this.busCode = busCode;
		}
		public String getCalledMethods() {
			return calledMethods;
		}
		public void setCalledMethods(String calledMethods) {
			this.calledMethods = calledMethods;
		}
		public String getMd5() {
			return md5;
		}
		public void setMd5(String md5) {
			this.md5 = md5;
		}
		public byte[] getSyncMethodResult() {
			return syncMethodResult;
		}
		public void setSyncMethodResult(byte[] syncMethodResult) {
			this.syncMethodResult = syncMethodResult;
		}
		public Date getCreateTime() {
			return createTime;
		}
		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}
		public Date getUpdateTime() {
			return updateTime;
		}
		public void setUpdateTime(Date updateTime) {
			this.updateTime = updateTime;
		}
		public Integer getLockVersion() {
			return lockVersion;
		}
		public void setLockVersion(Integer lockVersion) {
			this.lockVersion = lockVersion;
		}
		@Override
		public String toString() {
			return "IdempotentPo [srcAppId=" + srcAppId + ", srcBusCode="
					+ srcBusCode + ", srcTrxId=" + srcTrxId + ", appId="
					+ appId + ", busCode=" + busCode + ", calledMethods="
					+ calledMethods + ", md5=" + md5 + ", syncMethodResult="
					+ Arrays.toString(syncMethodResult) + ", createTime="
					+ createTime + ", updateTime=" + updateTime
					+ ", lockVersion=" + lockVersion + "]";
		}
	}

	public void saveIdempotentPo(EasyTransFilterChain filterChain,IdempotentPo idempotentPo) {
		
		JdbcTemplate jdbcTemplate = filterChain.getResource(JDBC_TEMPLATE);
		int update = jdbcTemplate.update(
				"INSERT INTO `idempotent` (`src_app_id`, `src_bus_code`, `src_trx_id`, `app_id`, `bus_code`, `called_methods`, `md5`, `sync_method_result`, `create_time`, `update_time` , `lock_version`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", 
				idempotentPo.getSrcAppId(),
				idempotentPo.getSrcBusCode(),
				idempotentPo.getSrcTrxId(),
				idempotentPo.getAppId(),
				idempotentPo.getBusCode(),
				idempotentPo.getCalledMethods(),
				idempotentPo.getMd5(),
				idempotentPo.getSyncMethodResult(),
				idempotentPo.getCreateTime(),
				idempotentPo.getUpdateTime(),
				idempotentPo.getLockVersion()
				);
		
		if(update != 1){
			throw new RuntimeException("update count exception!" + update);
		}
	}
	
	public void updateIdempotentPo(EasyTransFilterChain filterChain,IdempotentPo idempotentPo) {
		
		JdbcTemplate jdbcTemplate = filterChain.getResource(JDBC_TEMPLATE);
		int update = jdbcTemplate.update(
				"UPDATE `idempotent` SET `called_methods` = ?, `md5` = ?, `sync_method_result` = ?, `create_time` = ?, `update_time`  = ?, `lock_version` = `lock_version` + 1 WHERE `src_app_id` = ? AND `src_bus_code` = ? AND `src_trx_id` = ? AND `app_id` = ? AND `bus_code` = ? AND `lock_version` = ?;", 
				idempotentPo.getCalledMethods(),
				idempotentPo.getMd5(),
				idempotentPo.getSyncMethodResult(),
				idempotentPo.getCreateTime(),
				idempotentPo.getUpdateTime(),
				idempotentPo.getSrcAppId(),
				idempotentPo.getSrcBusCode(),
				idempotentPo.getSrcTrxId(),
				idempotentPo.getAppId(),
				idempotentPo.getBusCode(),
				idempotentPo.getLockVersion()
				);
		
		if(update != 1){
			throw new RuntimeException("Optimistic Lock Error Occour Or can not find the specific Record!" + idempotentPo);
		}
	}
}
