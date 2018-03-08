package com.taobao.pamirs.cache.framework.config;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * SetCache:需要配置缓存的方法加上此annotation.
 * 
 * @author qiudao
 * @version 1.0
 * @since 2014年10月30日
 */
@Target({ METHOD })
@Retention(RUNTIME)
public @interface SetCache {
	String beanName() default "";

	boolean useCache() default true;

	/** second unit */
	int expireTime() default 0;

}
