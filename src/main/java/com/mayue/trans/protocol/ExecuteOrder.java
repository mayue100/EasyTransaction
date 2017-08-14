package com.mayue.trans.protocol;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 定义方法执行顺序
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface ExecuteOrder {

    /**
     * 不执行方法的方法列表
     * 如TCC中 cancel方法执行，那么try方法应该不执行
     */
    String[] doNotExecuteAfter();

    /**
     * 如果业务方法不执行，迅速返回 fast return
     * 如在补偿型业务中，补偿业务操作未执行时（业务执行成功），调用补偿方法时，需要fast return
     */
    String[] ifNotExecutedReturnDirectly();

    /**
     * 方法是否与业务同步
     */
    boolean isSynchronousMethod() default false;
}
