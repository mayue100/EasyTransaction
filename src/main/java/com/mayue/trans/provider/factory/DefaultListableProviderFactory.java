package com.mayue.trans.provider.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mayue.trans.protocol.EasyTransRequest;
import com.mayue.trans.protocol.MessageBusinessProvider;
import org.springframework.context.ApplicationContext;

import com.mayue.trans.protocol.BusinessIdentifer;
import com.mayue.trans.protocol.BusinessProvider;
import com.mayue.trans.protocol.RpcBusinessProvider;
import com.mayue.trans.protocol.aft.AfterMasterTransMethod;
import com.mayue.trans.protocol.cps.CompensableMethod;
import com.mayue.trans.protocol.msg.BestEffortMessageHandler;
import com.mayue.trans.protocol.msg.ReliableMessageHandler;
import com.mayue.trans.protocol.tcc.TccMethod;
import com.mayue.trans.util.ReflectUtil;


public class DefaultListableProviderFactory implements ListableProviderFactory {
	
	private Map<Class<?>/*RpcBusiness、MessageBusiness */,Map<Class<?>/*TransactionType: TccMethod,BestEfforMessageHandler...*/,List<Object>/*Specific Business*/>> mapBusinessProvider = new HashMap<Class<?>, Map<Class<?>,List<Object>>>();
	
	private Map<String,Object> mapBusinessObject = new HashMap<String, Object>();
	private Map<String,Class<?>> mapBusinessInterface = new HashMap<String,Class<?>>();
	
	@Resource
	private ApplicationContext ctx;
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	private void initDefaultTypes(){
		
		{
			HashMap<Class<?>,List<Object>> rpcBusinessMap = new HashMap<Class<?>,List<Object>>();
			mapBusinessProvider.put(RpcBusinessProvider.class, rpcBusinessMap);
			
			rpcBusinessMap.put(TccMethod.class, new ArrayList<Object>());
			rpcBusinessMap.put(CompensableMethod.class, new ArrayList<Object>());
			rpcBusinessMap.put(AfterMasterTransMethod.class, new ArrayList<Object>());
		}
		
		{
			HashMap<Class<?>,List<Object>> messageBusinessMap = new HashMap<Class<?>,List<Object>>();
			mapBusinessProvider.put(MessageBusinessProvider.class, messageBusinessMap);
			
			messageBusinessMap.put(BestEffortMessageHandler.class, new ArrayList<Object>());
			messageBusinessMap.put(ReliableMessageHandler.class, new ArrayList<Object>());
		}
		
		//handle
		for(Entry<Class<?>, Map<Class<?>, List<Object>>> entry :mapBusinessProvider.entrySet()){
			Map<String, ?> beansOfType = ctx.getBeansOfType(entry.getKey());
			for(Object bean :beansOfType.values()){
				for(Entry<Class<?>, List<Object>> transactionTypeList:entry.getValue().entrySet()){
					Class<?> transactionType = transactionTypeList.getKey();
					if(transactionType.isAssignableFrom(bean.getClass())){
						transactionTypeList.getValue().add(bean);
						Class<? extends EasyTransRequest<?, ?>> requestClass = ReflectUtil.getRequestClass((Class<? extends BusinessProvider<?>>) bean.getClass());
						BusinessIdentifer businessIdentifer = ReflectUtil.getBusinessIdentifer(requestClass);
						if(businessIdentifer == null){
							throw new RuntimeException("request class did not add Annotation BusinessIdentifer,please add it! class:" + requestClass);
						}
						mapBusinessInterface.put(getBusKey(businessIdentifer.appId(), businessIdentifer.busCode()), transactionType);
						mapBusinessObject.put(getBusKey(businessIdentifer.appId(), businessIdentifer.busCode()), bean);
					}
				}
			}
		}
	}

	private String getBusKey(String appId, String busCode){
		return appId + "|" + busCode;
	}
	

	/* (non-Javadoc)
	 * @see com.yiqiniu.easytrans.register.ServiceRegister#getServiceRootKey()
	 */
	@Override
	public Set<Class<?>> getServiceRootKey(){
		return Collections.unmodifiableSet(mapBusinessProvider.keySet());
	}
	
	/* (non-Javadoc)
	 * @see com.yiqiniu.easytrans.register.ServiceRegister#getServiceTransactionTypeSet(java.lang.Class)
	 */
	@Override
	public Set<Class<?>> getServiceTransactionTypeSet(Class<?> rootType){
		Map<Class<?>, List<Object>> map = mapBusinessProvider.get(rootType);
		if(map != null){
			return  Collections.unmodifiableSet(map.keySet());
		}else{
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yiqiniu.easytrans.register.ServiceRegister#getServiceMap(java.lang.Class, java.lang.Class)
	 */
	@Override
	public List<Object> getServices(Class<?> root,Class<?> transactionType){
		Map<Class<?>, List<Object>> map = mapBusinessProvider.get(root);
		if(map != null){
			List<Object> list = map.get(transactionType);
			if(list != null){
				return Collections.unmodifiableList(list);
			}
		}
		return null;
	}


	@Override
	public Object getService(String appId, String busCode) {
		return mapBusinessObject.get(getBusKey(appId, busCode));
	}


	@Override
	public Class<?> getServiceInterface(String appId, String busCode) {
		return mapBusinessInterface.get(getBusKey(appId, busCode));
	}
	
}
