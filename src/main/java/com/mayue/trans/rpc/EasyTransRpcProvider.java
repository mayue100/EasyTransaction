package com.mayue.trans.rpc;

import java.util.List;
import java.util.Map;

import com.mayue.trans.protocol.RpcBusinessProvider;
import com.mayue.trans.filter.EasyTransFilter;
import com.mayue.trans.protocol.BusinessIdentifer;

public interface EasyTransRpcProvider {
    /**
     * start the service list offered
     * @param businessInterface the service interface
     * @param businessList detailServiceImpl
     */
    void startService(Class<?> businessInterface,Map<BusinessIdentifer,RpcBusinessProvider<?>> businessList);
    
    /**
     * add EasyTransFilter to RPC filters
     * @param filters ordered filter
     */
    void addEasyTransFilter(List<EasyTransFilter> filters);
}
