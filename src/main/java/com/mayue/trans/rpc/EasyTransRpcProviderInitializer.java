package com.mayue.trans.rpc;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mayue.trans.protocol.BusinessIdentifer;
import com.mayue.trans.protocol.BusinessProvider;
import com.mayue.trans.protocol.EasyTransRequest;
import com.mayue.trans.protocol.RpcBusinessProvider;
import com.mayue.trans.provider.factory.ListableProviderFactory;
import com.mayue.trans.filter.EasyTransFilterChainFactory;
import com.mayue.trans.util.ReflectUtil;

public class EasyTransRpcProviderInitializer {
	
	@Resource
	private EasyTransFilterChainFactory filterFactory;
	
	@Resource
	private EasyTransRpcProvider rpcProvider;
	
	@Resource
	private ListableProviderFactory wareHouse;
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	private void init(){
		
		//set filters
		rpcProvider.addEasyTransFilter(filterFactory.getDefaultFilters());;
		
		Set<Class<?>> typeSet = wareHouse.getServiceTransactionTypeSet(RpcBusinessProvider.class);
		
		//start services
		for(Class<?> transcationType:typeSet){
			List<Object> services = wareHouse.getServices(RpcBusinessProvider.class, transcationType);
			
			HashMap<BusinessIdentifer,RpcBusinessProvider<?>> map = new HashMap<BusinessIdentifer, RpcBusinessProvider<?>>(services.size());
			for(Object serviceObject:services){
				RpcBusinessProvider<?> provider = (RpcBusinessProvider<?>) serviceObject;
				Class<? extends EasyTransRequest<?, ?>> requestClass = ReflectUtil.getRequestClass((Class<? extends BusinessProvider<?>>) provider.getClass());
				BusinessIdentifer businessIdentifer = ReflectUtil.getBusinessIdentifer(requestClass);
				map.put(businessIdentifer, provider);
			}
			
			rpcProvider.startService(transcationType, map);
		}
	}
	
}
