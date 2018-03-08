package com.taobao.pamirs.cache.util;

import java.lang.reflect.Field;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

/**
 * 代理相关辅助类
 * 
 * @author xiaocheng 2012-11-22
 */
public class AopProxyUtil {

	/**
	 * 取得被代理的原始对象
	 * 
	 * @param proxy
	 * @return
	 */
	public static Object getPrimitiveProxyTarget(Object proxy) throws Exception {

		if (!AopUtils.isAopProxy(proxy)) {
			return proxy;
		}

		if (AopUtils.isJdkDynamicProxy(proxy)) {
			return getJdkDynamicProxyTargetObject(proxy);
		} else {
			return getCglibProxyTargetObject(proxy);
		}

	}

	private static Object getJdkDynamicProxyTargetObject(Object proxy)
			throws Exception {
		Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
		h.setAccessible(true);
		AopProxy aopProxy = (AopProxy) h.get(proxy);

		Field advised = aopProxy.getClass().getDeclaredField("advised");
		advised.setAccessible(true);

		Object target = ((AdvisedSupport) advised.get(aopProxy))
				.getTargetSource().getTarget();

		return target;
	}

	private static Object getCglibProxyTargetObject(Object proxy)
			throws Exception {
		Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
		h.setAccessible(true);
		Object dynamicAdvisedInterceptor = h.get(proxy);

		Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField(
				"advised");
		advised.setAccessible(true);

		Object target = ((AdvisedSupport) advised
				.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();

		return target;
	}

}
