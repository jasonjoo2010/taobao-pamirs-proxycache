package com.taobao.pamirs.cache.load;

import com.taobao.pamirs.cache.framework.config.CacheConfig;

/**
 * 缓存配置获取接口
 * 
 * @author xiaocheng 2012-11-12
 */
public interface ICacheConfigService {

	/**
	 * 加载缓存配置
	 * 
	 * @return
	 */
	CacheConfig loadConfig();
	
	/**
	 * 运行期重载配置
	 * 【按照proxy工作原理在运行期变化缓存配置，非原子操作，重载配置过程可能读取到新老并存的配置,
	 *   可通过notCacheWhenReload参数执行替换瞬间不走缓存】
	 * @return
	 */
	boolean runtimeReloadConfig(CacheConfig newConfig);
	

	/**
	 * 自动修正默认配置
	 * 
	 * @param cacheConfig
	 */
	void autoFillCacheConfig(CacheConfig cacheConfig);

	/**
	 * 校验配置合法性
	 * 
	 * @param cacheConfig
	 */
	void verifyCacheConfig(CacheConfig cacheConfig);
}
