package com.taobao.pamirs.cache.load.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.util.CollectionUtils;

import com.taobao.pamirs.cache.extend.jmx.annotation.JmxClass;
import com.taobao.pamirs.cache.framework.config.CacheBean;
import com.taobao.pamirs.cache.framework.config.CacheCleanBean;
import com.taobao.pamirs.cache.framework.config.CacheCleanMethod;
import com.taobao.pamirs.cache.framework.config.CacheConfig;
import com.taobao.pamirs.cache.framework.config.CacheModule;
import com.taobao.pamirs.cache.framework.config.CleanCache;
import com.taobao.pamirs.cache.framework.config.MethodConfig;
import com.taobao.pamirs.cache.framework.config.SetCache;
import com.taobao.pamirs.cache.load.LoadConfigException;
import com.taobao.pamirs.cache.util.ConfigUtil;
import com.taobao.pamirs.cache.util.PackageUtil;

/**
 * TODO 基于annotation的cacheManager
 * 
 * @author qiudao
 * @version 1.0
 * @since 2014年10月30日
 */
@JmxClass
public class AnnotationConfigCacheManager extends LocalConfigCacheManager {

	private List<String> packagePaths;

	public void setPackagePaths(List<String> packagePaths) {
		this.packagePaths = packagePaths;
	}

	/**
	 * 加载加载缓存配置
	 * 
	 * @return
	 */
	public CacheConfig loadConfig() {
		CacheConfig cacheConfig = null;
		try {
			cacheConfig = super.loadConfig();
		} catch (LoadConfigException e) {
			// ignore
		} catch (IllegalArgumentException e) {
			// ignore
		}

		List<CacheModule> cacheModules = getCacheModules();
		if (cacheConfig == null && cacheModules.size() <= 0) {
			throw new LoadConfigException("非法的缓存配置，CacheModule列表为空");
		}

		// TODO merge;
		if (cacheConfig == null) {
			cacheConfig = new CacheConfig();
			cacheConfig.setStoreType(getStoreType());
			cacheConfig.setStoreMapCleanTime(getMapCleanTime());
			cacheConfig.setStoreRegion(getStoreRegion());
			cacheConfig.setStoreTairNameSpace(getTairNameSpace());
		}
		for (CacheModule cacheModule : cacheModules) {
			for (CacheBean cb : cacheModule.getCacheBeans()) {
				boolean exists = false;
				CacheBean existCacheBean = null;
				for (CacheBean cb2 : cacheConfig.getCacheBeans()) {
					if (cb2.getBeanName().equals(cb.getBeanName())) {
						exists = true;
						existCacheBean = cb2;
						break;
					}
				}
				if (!exists) {
					cacheConfig.getCacheBeans().add(cb);
				} else {
					boolean existsMethod = false;
					for (MethodConfig mc : cb.getCacheMethods()) {
						for (MethodConfig mc2 : existCacheBean.getCacheMethods()) {
							if (mc2.isMe(mc.getMethodName(), mc.getParameterTypes())) {
								existsMethod = true;
							}
						}

						if (!existsMethod) {
							existCacheBean.getCacheMethods().add(mc);
						}
					}
				}
			}

			for (CacheCleanBean ccb : cacheModule.getCacheCleanBeans()) {
				boolean exists = false;
				CacheCleanBean existCacheCleanBean = null;
				for (CacheCleanBean cb2 : cacheConfig.getCacheCleanBeans()) {
					if (cb2.getBeanName().equals(ccb.getBeanName())) {
						exists = true;
						existCacheCleanBean = cb2;
						break;
					}
				}
				if (!exists) {
					cacheConfig.getCacheCleanBeans().add(ccb);
				} else {
					boolean existsMethod = false;
					for (CacheCleanMethod ccm : ccb.getMethods()) {
						for (CacheCleanMethod ccm2 : existCacheCleanBean.getMethods()) {
							if (ccm2.isMe(ccm.getMethodName(), ccm.getParameterTypes())) {
								existsMethod = true;
							}
						}

						if (!existsMethod) {
							existCacheCleanBean.getMethods().add(ccm);
						}
					}
				}
			}
		}

		return cacheConfig;
	}

	/**
	 * 从文件中获取配置文件信息
	 * 
	 * @return
	 * @throws Exception
	 */
	private List<CacheModule> getCacheModules() {
		if (packagePaths == null || packagePaths.size() <= 0) {
			throw new IllegalArgumentException("非法配置文件路径的参数，配置文件列表不能为空");
		}

		List<CacheModule> cacheModuleList = new ArrayList<CacheModule>();
		for (String packPath : packagePaths) {
			Set<Class<?>> clazzs = PackageUtil.getClasses(packPath);
			if (CollectionUtils.isEmpty(clazzs)) {
				continue;
			}

			for (Class<?> tmp : clazzs) {
				Method[] ms = tmp.getMethods();
				if (ms == null || ms.length == 0) {
					continue;
				}

				CacheModule cacheModule = null;
				for (Method mth : ms) {
					SetCache setCache = mth.getAnnotation(SetCache.class);
					if (setCache != null) {
						CacheBean cacheBean = ConfigUtil.getCacheBean(setCache, mth);
						if (cacheBean != null) {
							if (cacheModule == null) {
								cacheModule = new CacheModule();
							}
							cacheModule.getCacheBeans().add(cacheBean);
						}

					}
					CleanCache cleanCache = mth.getAnnotation(CleanCache.class);
					if (cleanCache != null) {
						CacheCleanBean cacheCleanBean = ConfigUtil.getCacheCleanBean(cleanCache, mth);
						if (cacheCleanBean != null) {
							if (cacheModule == null) {
								cacheModule = new CacheModule();
							}
							cacheModule.getCacheCleanBeans().add(cacheCleanBean);
						}
					}

				}
				if (cacheModule != null)
					cacheModuleList.add(cacheModule);
			}
		}

		return cacheModuleList;
	}
}
