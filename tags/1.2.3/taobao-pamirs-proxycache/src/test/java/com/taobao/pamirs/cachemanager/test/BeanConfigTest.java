package com.taobao.pamirs.cachemanager.test;

import org.junit.Assert;
import org.junit.Test;

import com.taobao.pamirs.cache.config.BeanCacheCleanConfig;
import com.taobao.pamirs.cache.config.BeanCacheConfig;

public class BeanConfigTest {
	@Test
	public void testCachConfig() {
		String defaultItem = "returnType=serializable," + " storeType=tair,"
				+ "expireTimes=60000," + "loadDataMethodName=loadAllService,"
				+ " isInitialData=true,"
				+ " cleanTime=0,10,20,30,40,50 * * * * ? *,"
				+ "groupName=ª˘¥° ˝æ›  ";
		String str = "beanName=prodSubScriptionDAO,"
				+ "methodName=getProdSubScriptions," + "parameterTypes={long},"
				+ "cacheName=prodSubScriptionCache,";
		BeanCacheConfig config = new BeanCacheConfig(defaultItem, str);
		System.out.println(config.toString());
//		Assert.assertTrue("ª∫¥Ê≈‰÷√Ω‚Œˆ¥ÌŒÛ£∫", config.toString().equalsIgnoreCase(str));
	}

	@Test
	public void testCleanCachConfig() {
		String[] strs = {
		// "beanName=testBean,methodName=update,cacheNames=serviceCache$productCache,keyMatch=1$2.id",
		// "beanName=testBean,methodName=update,cacheNames=serviceCache$productCache,keyMatch=1.name$2.id",
		// "beanName=testBean,methodName=update,cacheNames=serviceCache$productCache,keyMatch=1$2.id%3",
		"beanName=testBean,methodName=updateService,parameterTypes={long},cacheCleanCodes={testBean$getService${long};testBean$getProduct${long}},keyMatch=1" };
		for (String s : strs) {
			BeanCacheCleanConfig config = new BeanCacheCleanConfig(s);
			System.out.println(config.toString());
			Assert.assertTrue("ª∫¥Ê≈‰÷√Ω‚Œˆ¥ÌŒÛ£∫", config.toString()
					.equalsIgnoreCase(s));
		}
	}
}
