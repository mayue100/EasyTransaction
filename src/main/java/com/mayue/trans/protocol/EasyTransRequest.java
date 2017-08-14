package com.mayue.trans.protocol;

import java.io.Serializable;

import com.mayue.trans.executor.EasyTransExecutor;

/**
 * 基础父类实体
 * @param <R> 序列化
 * @param <E> 方法执行器
 */
public interface EasyTransRequest<R extends Serializable,E extends EasyTransExecutor> extends IdempotentTypeDeclare,Serializable{
	
}
