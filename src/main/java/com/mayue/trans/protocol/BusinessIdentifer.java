package com.mayue.trans.protocol;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * 业务识别注解，指定appId，业务代码
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface BusinessIdentifer {

    /**
     * appId
     */
    String appId();

    /**
     * 业务代码
     *
     * @return
     */
    String busCode();

    /**
     * rpc 超时时间
     *
     * @return
     */
    int rpcTimeOut() default 0;
}
