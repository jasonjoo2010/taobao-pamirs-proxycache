package com.taobao.pamirs.cache.framework.config;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * CleanCache:��Ҫ�������ķ������ϴ�annotation.
 * 
 * @author qiudao
 * @version 1.0
 * @since 2014��10��30��
 */
@Target({ METHOD })
@Retention(RUNTIME)
public @interface CleanCache {
	String beanName() default "";

	/** �����methodName��cacheBean�����Ҷ�Ӧ��methodConfig */
	String[] cleanMethod();
}
