package com.taobao.pamirs.cache.load.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.Serializable;
import java.util.ArrayList;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.taobao.pamirs.cache.framework.CacheProxy;
import com.taobao.pamirs.cache.framework.config.MethodConfig;
import com.taobao.pamirs.cache.load.testbean.AServiceImpl;
import com.taobao.pamirs.cache.util.CacheCodeUtil;

@SpringApplicationContext({ "/store/tair-store.xml", "/load/annotation-cache-spring.xml" })
public class AnnotationCacheManagerTest extends UnitilsJUnit4 {

	@SpringBeanByName
	private AnnotationConfigCacheManager cacheManager;

	@SpringBeanByName
	private AServiceImpl aService;

	@Test
	public void testGetCacheProxy() {
		MethodConfig methodConfig = new MethodConfig();
		methodConfig.setMethodName("firstHaveValue");

		ArrayList<Class<?>> parameterTypes = new ArrayList<Class<?>>();
		parameterTypes.add(String.class);
		methodConfig.setParameterTypes(parameterTypes);

		String key = CacheCodeUtil.getCacheAdapterKey(cacheManager.getStoreRegion(), "aService", methodConfig);

		CacheProxy<Serializable, Serializable> cacheProxys = cacheManager.getCacheProxy(key);
		cacheProxys.get(key, "127.0.0.1");

		assertThat(cacheProxys, notNullValue());
		assertThat(cacheProxys.getKey(), equalTo(key));
	}

	@Test
	public void testCache() {
		String md5Str = aService.md5Name("123");
		md5Str = aService.md5Name("123");
		System.out.println(md5Str);
	}

}
