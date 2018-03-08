package com.taobao.pamirs.cache.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.pamirs.cache.MBeanManagerFactory;
import com.taobao.pamirs.cache.cache.Cache;
import com.taobao.pamirs.cache.cache.CacheMbean;
import com.taobao.pamirs.cache.config.BeanCacheCleanConfig;
import com.taobao.pamirs.cache.config.BeanCacheConfig;
import com.taobao.pamirs.cache.timer.TimeTaskManager;

public class CacheManager {

	private static Log log = LogFactory.getLog(CacheManager.class);

	static {
		try {
			MBeanManagerFactory.registerMBean(CacheManagerMBean.MBEAN_NAME
					+ "name=cacheManager", new CacheManagerMBean());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	private static TimeTaskManager timerTaskManager = new TimeTaskManager();

	/**
	 * ͨ�� CacheConfig.xml ����ע��
	 * **/
	private List<String> cacheConfig;
	private List<String> cacheCleanConfig;

	/**
	 * ��Ҫ���л������� cacheCode Map
	 */
	private Map<String, BeanCacheConfig> beanCacheConfigMap = new HashMap<String, BeanCacheConfig>();
	private Map<String, BeanCacheCleanConfig> beanCacheCleanConfigMap = new HashMap<String, BeanCacheCleanConfig>();

	public Map<String, BeanCacheConfig> getBeanCacheConfigMap() {
		return this.beanCacheConfigMap;
	}

	public Map<String, BeanCacheCleanConfig> getBeanCacheCleanConfigMap() {
		return this.beanCacheCleanConfigMap;
	}

	// Bean PointCut Set
	private Set<String> cacheBeanNameSet = new HashSet<String>();

	public Set<String> getCacheBeanNameSet() {
		return this.cacheBeanNameSet;
	}
	/**
	 * ͨ�� CacheConfig.xml ����ע�뻺������Ĭ��ֵ
	 */
	private String defaultCacheConfig;
	public void setDefaultCacheConfig(String defaultCacheConfig) {
		this.defaultCacheConfig = defaultCacheConfig;
	}

	// ͨ�����涨�����ɻ��� Map �ṹ
	public void setCacheConfig(List<String> aCcheConfig) {
		this.cacheConfig = aCcheConfig;
		for (String item : this.cacheConfig) {
			BeanCacheConfig config = new BeanCacheConfig(this.defaultCacheConfig,item);
			if (!beanCacheConfigMap.containsKey(config.getCacheCode())) {
				beanCacheConfigMap.put(config.getCacheCode(), config);
				cacheBeanNameSet.add(config.getBeanName());
			} else {
				throw new RuntimeException("CacheCode �ظ�"
						+ config.getCacheCode());
			}
		}
	}

	// ͨ������ clear �������ɻ��� clear Map �ṹ
	public void setCacheCleanConfig(List<String> aCcheConfig) {
		this.cacheCleanConfig = aCcheConfig;
		for (String item : this.cacheCleanConfig) {
			BeanCacheCleanConfig config = new BeanCacheCleanConfig(item);
			if (!beanCacheCleanConfigMap.containsKey(config.getCacheCode())) {
				beanCacheCleanConfigMap.put(config.getCacheCode(), config);
				cacheBeanNameSet.add(config.getBeanName());
			} else {
				throw new RuntimeException("Cache clean CacheCode �ظ�"
						+ config.getCacheCode());
			}
		}
	}

	// �����Ƿ��ʼ����־λ.
	private boolean cacheInitFlag;

	public void setCacheInitFlag(boolean cacheInitFlag) {
		this.cacheInitFlag = cacheInitFlag;

		if (this.cacheInitFlag) {
			// ���л����ʼ��.
			for (Iterator<String> iterator = beanCacheConfigMap.keySet()
					.iterator(); iterator.hasNext();) {
				String key = iterator.next();
				BeanCacheConfig beanCacheConfig = beanCacheConfigMap.get(key);
				this.createCache(beanCacheConfig);
			}
		}
	}
	
	// �����Ƿ��ʼ����־λ.
	private boolean cacheUseFlag;

	public void setCacheUseFlag(boolean cacheUseFlag) {
		this.cacheUseFlag = cacheUseFlag;
	}
	public boolean isUseCache(){
		return this.cacheUseFlag;
	}
	
	// Map<cacheCode , Cache<String, Object>>
	// cacheCode = beanName:methodName:parameterTypes
	private static Map<String, Cache<String, Object>> caches = new ConcurrentHashMap<String, Cache<String, Object>>();
	
	/**
	 * ͨ�� CacheName �洢 CacheCode ��ӳ�� map
	 * **/
	private static Map<String, String> cacheNametoCode = new ConcurrentHashMap<String,String>();

	public static TimeTaskManager getTimeTaskManager() {
		return timerTaskManager;
	}

	/**
	 * ��ȡ����byCode
	 * 
	 * @param cacheName
	 * @return
	 */
	public static Cache<String, Object> getCache(String cacheCode) {
		return caches.get(cacheCode);
	}

	/**
	 * ��ȡ����byName
	 * 
	 * @param cacheName
	 * @return
	 */
	public static Cache<String, Object> getCacheByName(String cacheName) {		
		return caches.get(cacheNametoCode.get(cacheName));
	}
	
	/**
	 * ��������
	 * 
	 * @param cacheName
	 * @param groupName
	 * @param createAddr
	 * @param beanFactory
	 * @param loadAllBeanName
	 * @param loadAllMethodName
	 * @param isInitialCache
	 * @return
	 */
	public Cache<String, Object> createCache(BeanCacheConfig beanCacheConfig) {

		if (caches.containsKey(beanCacheConfig.getCacheCode())) {
			throw new RuntimeException("�����ظ����壬���������ļ���"
					+ beanCacheConfig.getCacheName());
		}
		Cache<String, Object> cache = null;

		cache = new Cache<String, Object>(beanCacheConfig);

		caches.put(beanCacheConfig.getCacheCode(), cache);
		cacheNametoCode.put(beanCacheConfig.getCacheName(), beanCacheConfig.getCacheCode());

		String mbeanName = CacheManagerMBean.MBEAN_NAME
				+ "name="+beanCacheConfig.getCacheName();

		CacheMbean<String, Object> cacheMbean = new CacheMbean<String, Object>(cache); 
		
		try {
			MBeanManagerFactory.registerMBean(mbeanName, cacheMbean);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return cache;
	}

	public static String[] getCacheNames() {
		if (cacheNametoCode != null && cacheNametoCode.size() > 0) {
			return cacheNametoCode.keySet().toArray(new String[0]);
		} else {
			return new String[0];
		}
	}
}
