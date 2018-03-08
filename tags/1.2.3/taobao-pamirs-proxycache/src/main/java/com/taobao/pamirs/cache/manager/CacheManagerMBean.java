package com.taobao.pamirs.cache.manager;

import java.util.Arrays;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

import com.taobao.pamirs.cache.AbstractDynamicMBean;
import com.taobao.pamirs.cache.cache.Cache;

public class CacheManagerMBean extends AbstractDynamicMBean {
	
	public static String MBEAN_NAME = "HJ-Cache:";
	
	public String getCacheNames() {
		String[] cacheNames = CacheManager.getCacheNames();
		Arrays.sort(cacheNames);
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < cacheNames.length; i++) {
			Cache<String, Object> cache = CacheManager
					.getCache(cacheNames[i]);
			result.append(cache.getCacheName()+" : " + cache.getCacheInfo() + "\n");
		}
		return result.toString();
	}
	public String getCacheNamesOrderByBeanName() {
		String[] cacheNames = CacheManager.getCacheNames();
		Arrays.sort(cacheNames);
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < cacheNames.length; i++) {
			Cache<String, Object> item = CacheManager
					.getCache(cacheNames[i]);
			result.append(cacheNames[i] + "#" + item.getCacheInfo() + "\n");
		}
		return result.toString();
	}
	protected void buildDynamicMBeanInfo() {
		MBeanAttributeInfo[] dAttributes = new MBeanAttributeInfo[] { new MBeanAttributeInfo(
				"cacheNames", "java.lang.String", "缓存清单", true, false, false) };

		MBeanOperationInfo[] dOperations = new MBeanOperationInfo[] { new MBeanOperationInfo(
				"getCacheNames", "获取缓存清单", new MBeanParameterInfo[] {},
				"String", MBeanOperationInfo.ACTION) };
		dMBeanInfo = new MBeanInfo(this.getClass().getName(), "HJ-CacheManager",
				dAttributes, null, dOperations, null);
	}
}
