package com.taobao.pamirs.cache.framework.aop.advice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.CollectionUtils;

import com.taobao.pamirs.cache.CacheManager;
import com.taobao.pamirs.cache.framework.CacheProxy;
import com.taobao.pamirs.cache.framework.config.CacheBean;
import com.taobao.pamirs.cache.framework.config.MethodConfig;
import com.taobao.pamirs.cache.util.CacheCodeUtil;

/**
 * 通知处理类
 * 
 * @author qiudao
 */
public class AnnotationCacheManagerRoundAdvice extends CacheManagerRoundAdvice {

	public AnnotationCacheManagerRoundAdvice(CacheManager cacheManager, String beanName) {
		super(cacheManager, beanName);
	}

	public Object invoke(MethodInvocation invocation) throws Throwable {
		return super.invoke(invocation);
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
	protected void cleanCache(String beanName, List<MethodConfig> cacheCleanMethods, MethodInvocation invocation,
			String storeRegion, String ip) throws Throwable {
		if (cacheCleanMethods == null || cacheCleanMethods.isEmpty())
			return;

		for (MethodConfig methodConfig : cacheCleanMethods) {

			String adapterKey = CacheCodeUtil.getCacheAdapterKey(storeRegion, beanName, methodConfig);
			CacheProxy<Serializable, Serializable> cacheAdapter = getCacheManager().getCacheProxy(adapterKey);

			if (cacheAdapter != null) {
				List<MethodConfig> cacheCleanMethodConfigs = getCacheCleanMethodConfigs(methodConfig, invocation);
				for (MethodConfig mc : cacheCleanMethodConfigs) {
					String cacheCode = CacheCodeUtil.getCacheCode(storeRegion, beanName, mc, invocation.getArguments());// 这里的invocation直接用主bean的，因为清理的bean的参数必须和主bean保持一致
					cacheAdapter.remove(cacheCode, ip);
				}

			}
		}
	}

	private List<MethodConfig> getCacheCleanMethodConfigs(MethodConfig oldMethodConfig, MethodInvocation invocation) {
		List<MethodConfig> mcs = new ArrayList<MethodConfig>();
		if (invocation.getArguments().length == 0) {
			mcs.add(oldMethodConfig);
			return mcs;
		}

		for (CacheBean tmp : getCacheManager().getCacheConfig().getCacheBeans()) {
			if (!tmp.getBeanName().equals(getBeanName()))
				continue;

			List<MethodConfig> cacheMethods = tmp.getCacheMethods();
			if (CollectionUtils.isEmpty(cacheMethods))
				return mcs;

			for (MethodConfig mc : cacheMethods) {
				if (mc.getMethodName().equals(oldMethodConfig.getMethodName())
				// TODO 是否需要判断参数是否一致
				// && mc.getParameterTypes().equals(invocation.getMethod().getParameterTypes())
				) {
					mcs.add(mc);
				}
			}
		}

		return mcs;
	}
}
