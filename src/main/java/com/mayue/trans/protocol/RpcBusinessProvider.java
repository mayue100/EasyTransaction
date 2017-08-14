package com.mayue.trans.protocol;

/**
 * rpc 业务提供者
 * @param <P>
 */
public interface RpcBusinessProvider<P extends EasyTransRequest<?, ?>> extends BusinessProvider<P>{
}
