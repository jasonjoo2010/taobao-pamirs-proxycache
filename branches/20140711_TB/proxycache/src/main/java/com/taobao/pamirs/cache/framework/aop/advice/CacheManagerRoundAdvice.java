package com.taobao.pamirs.cache.framework.aop.advice;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;

import com.taobao.pamirs.cache.CacheManager;
import com.taobao.pamirs.cache.framework.CacheProxy;
import com.taobao.pamirs.cache.framework.config.CacheConfig;
import com.taobao.pamirs.cache.framework.config.MethodConfig;
import com.taobao.pamirs.cache.util.CaCheProxyLog;
import com.taobao.pamirs.cache.util.CacheCodeUtil;
import com.taobao.pamirs.cache.util.ConfigUtil;

/**
 * 通知处理类
 * 
 * @author xuannan
 * @author xiaocheng 2012-10-30
 */
public class CacheManagerRoundAdvice implements MethodInterceptor, Advice {

	private static final Log log = CaCheProxyLog.LOGGER_DEFAULT;

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
			log.error("CacheManager:切面解析配置出错:" + beanName + "#"
					+ invocation.getMethod().getName(), e);
			return invocation.proceed();
		}

		String fromHsfIp = "";// hsf consumer ip
		try {
			fromHsfIp = (String) invocation.getThis().getClass()
					.getMethod("getCustomIp").invoke(invocation.getThis());
		} catch (NoSuchMethodException e) {
			log.debug("接口没有实现HSF的getCustomIp方法，取不到Consumer IP, beanName="
					+ beanName);
		}

		try {
			// 1. cache
			if (cacheManager.isUseCache() 
					&& cacheMethod != null&&!cacheMethod.isLocalHostNotCache()) {
				String adapterKey = CacheCodeUtil.getCacheAdapterKey(
						storeRegion, beanName, cacheMethod);
				CacheProxy<Serializable, Serializable> cacheAdapter = cacheManager
						.getCacheProxy(adapterKey);

				String cacheCode = CacheCodeUtil.getCacheCode(storeRegion,
						beanName, cacheMethod, invocation.getArguments());

				return useCache(cacheAdapter, cacheCode,
						 invocation, fromHsfIp);
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
//			log.error("CacheManager:出错:" + beanName + "#"
//					+ invocation.getMethod().getName(), e);
			throw e;
		}
	}

	/**
	 * 缓存处理
	 * 
	 * @param cacheAdapter
	 * @param cacheCode
	 * @param invocation
	 * @return
	 * @throws Throwable
	 */
	private Object useCache(
			CacheProxy<Serializable, Serializable> cacheAdapter,
			String cacheCode,MethodInvocation invocation,
			String ip) throws Throwable {
		if (cacheAdapter == null)
			return invocation.proceed();

		Object response = cacheAdapter.get(cacheCode, ip);

		if (response == null) {
			response = invocation.proceed();

			if (response == null)// 如果原生方法结果为null，不put到缓存了
				return response;
			cacheAdapter.put(cacheCode, (Serializable) response, ip);
			
			//兼容
			if(cacheManager.getOldCacheManager()!=null
					&&cacheManager.getOldCacheManager().isCompatible()){
				cacheManager.getOldCacheManager().put(this.beanName, invocation, response);
			}
			
		}

		return response;
	}

	/**
	 * 清除缓存处理
	 * 
	 * @param cacheCleanBean
	 * @param invocation
	 * @param storeRegion
	 * @return
	 * @throws Throwable
	 */
	protected void cleanCache(String beanName,
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
						beanName, methodConfig, invocation.getArguments());// 这里的invocation直接用主bean的，因为清理的bean的参数必须和主bean保持一致
				cacheAdapter.remove(cacheCode, ip);
				
				//兼容
				if(cacheManager.getOldCacheManager()!=null
						&&cacheManager.getOldCacheManager().isCompatible()){
					cacheManager.getOldCacheManager().remove(beanName, methodConfig, invocation.getArguments());
				}
			}
		}
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	
}
