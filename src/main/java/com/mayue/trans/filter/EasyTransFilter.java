package com.mayue.trans.filter;

import com.mayue.trans.protocol.EasyTransRequest;


public interface EasyTransFilter {

	/**
	 * do invoke filter.
	 * 
	 * <code>
	 * // before filter
     * Result result = invoker.invoke(invocation);
     * // after filter
     * return result;
     * </code>
     * 
	 * @param invoker service
	 * @param invocation invocation.
	 * @return invoke result.
	 */
	EasyTransResult invoke(EasyTransFilterChain filterChain, EasyTransRequest<?, ?> request);

}
