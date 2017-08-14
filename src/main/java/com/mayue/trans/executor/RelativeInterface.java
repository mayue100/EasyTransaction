package com.mayue.trans.executor;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * 声明相关业务实现的接口
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface RelativeInterface{
	Class<?> value();
}
