package com.taobao.pamirs.cache.framework.aop.advice;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.pamirs.cache.CacheManager;
import com.taobao.pamirs.cache.framework.CacheProxy;
import com.taobao.pamirs.cache.framework.config.CacheConfig;
import com.taobao.pamirs.cache.framework.config.MethodConfig;
import com.taobao.pamirs.cache.util.CacheCodeUtil;
import com.taobao.pamirs.cache.util.ConfigUtil;

/**
 * ֪ͨ������
 * 
 * @author xuannan
 * @author xiaocheng 2012-10-30
 */
public class CacheManagerRoundAdvice implements MethodInterceptor, Advice {

	private static final Log log = LogFactory
			.getLog(CacheManagerRoundAdvice.class);

	private CacheManager cacheManager;
	private String beanName;

	public CacheManagerRoundAdvice(CacheManager cacheManager, String beanName) {
		this.cacheManager = cacheManager;
		this.beanName = beanName;
	}

	public Object invoke(MethodInvocation invocation) throws Throwable {

		MethodConfig cacheMethod = null;
		List<MethodConfig> cacheCleanMethods = null;
		String storeRegion = "";

		Method method = invocation.getMethod();
		String methodName = method.getName();

		try {
			CacheConfig cacheConfig = cacheManager.getCacheConfig();
			storeRegion = cacheConfig.getStoreRegion();

			List<Class<?>> parameterTypes = Arrays.asList(method
					.getParameterTypes());

			cacheMethod = ConfigUtil.getCacheMethod(cacheConfig, beanName,
					methodName, parameterTypes);
			cacheCleanMethods = ConfigUtil.getCacheCleanMethods(cacheConfig,
					beanName, methodName, parameterTypes);

		} catch (Exception e) {
			log.error("CacheManager:����������ó���:" + beanName + "#"
					+ invocation.getMethod().getName(), e);
			return invocation.proceed();
		}

		String fromHsfIp = "";// hsf consumer ip
		try {
			fromHsfIp = (String) invocation.getThis().getClass()
					.getMethod("getCustomIp").invoke(invocation.getThis());

			if (fromHsfIp == null) {
				log.warn("�ӿڵ�getCustomIp����ȡ����Consumer IP, beanName="
						+ beanName + ", method=" + methodName);
				fromHsfIp = "";// ��Ϊ�գ���Ҫ��ʾnull
			}
		} catch (NoSuchMethodException e) {
			log.warn("�ӿ�û��ʵ��HSF��getCustomIp������ȡ����Consumer IP, beanName="
					+ beanName);
		}

		try {
			// 1. cache
			if (cacheManager.isUseCache() && cacheMethod != null) {
				String adapterKey = CacheCodeUtil.getCacheAdapterKey(
						storeRegion, beanName, cacheMethod);
				CacheProxy<Serializable, Serializable> cacheAdapter = cacheManager
						.getCacheProxy(adapterKey);

				String cacheCode = CacheCodeUtil.getCacheCode(storeRegion,
						beanName, cacheMethod, invocation.getArguments());

				return useCache(cacheAdapter, cacheCode,
						cacheMethod.getExpiredTime(), invocation, fromHsfIp);
			}

			// 2. cache clean
			if (cacheCleanMethods != null) {
				try {
					return invocation.proceed();
				} finally {
					cleanCache(beanName, cacheCleanMethods, invocation,
							storeRegion, fromHsfIp);
				}
			}

			// 3. do nothing
			return invocation.proceed();
		} catch (Exception e) {
			log.error("CacheManager:����:" + beanName + "#"
					+ invocation.getMethod().getName(), e);
			throw e;
		}
	}

	/**
	 * ���洦��
	 * 
	 * @param cacheAdapter
	 * @param cacheCode
	 * @param expireTime
	 * @param invocation
	 * @return
	 * @throws Throwable
	 */
	private Object useCache(
			CacheProxy<Serializable, Serializable> cacheAdapter,
			String cacheCode, Integer expireTime, MethodInvocation invocation,
			String ip) throws Throwable {
		if (cacheAdapter == null)
			return invocation.proceed();

		Object response = cacheAdapter.get(cacheCode, ip);

		if (response == null) {
			response = invocation.proceed();

			if (response == null)// ���ԭ���������Ϊnull����put��������
				return response;

			if (expireTime == null) {
				cacheAdapter.put(cacheCode, (Serializable) response, ip);
			} else {
				cacheAdapter.put(cacheCode, (Serializable) response,
						expireTime, ip);
			}
		}

		return response;
	}

	/**
	 * ������洦��
	 * 
	 * @param cacheCleanBean
	 * @param invocation
	 * @param storeRegion
	 * @return
	 * @throws Throwable
	 */
	private void cleanCache(String beanName,
			List<MethodConfig> cacheCleanMethods, MethodInvocation invocation,
			String storeRegion, String ip) throws Throwable {
		if (cacheCleanMethods == null || cacheCleanMethods.isEmpty())
			return;

		for (MethodConfig methodConfig : cacheCleanMethods) {

			String adapterKey = CacheCodeUtil.getCacheAdapterKey(storeRegion,
					beanName, methodConfig);
			CacheProxy<Serializable, Serializable> cacheAdapter = cacheManager
					.getCacheProxy(adapterKey);

			if (cacheAdapter != null) {
				String cacheCode = CacheCodeUtil.getCacheCode(storeRegion,
						beanName, methodConfig, invocation.getArguments());// �����invocationֱ������bean�ģ���Ϊ�����bean�Ĳ����������bean����һ��
				cacheAdapter.remove(cacheCode, ip);
			}
		}
	}

}
