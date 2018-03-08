package com.taobao.pamirs.cache.framework.config;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * CleanCache:需要清除缓存的方法加上此annotation.
 * 
 * @author qiudao
 * @version 1.0
 * @since 2014年10月30日
 */
@Target({ METHOD })
@Retention(RUNTIME)
public @interface CleanCache {
	String beanName() default "";

	/** 会根据methodName从cacheBean里面找对应的methodConfig */
	String[] cleanMethod();
}
