package com.taobao.pamirs.cache.util;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * ������ز�����
 * 
 * @author xiaocheng 2012-11-22
 */
public class AopProxyUtilTest {

	ApplicationContext context = new ClassPathXmlApplicationContext(
			new String[] { "/store/tair-store.xml", "/load/cache-spring.xml" });

	@Test
	public void testGetPrimitiveProxyTarget() throws Exception {
		Object bean = context.getBean("aService");
		assertThat(bean, notNullValue());

		// cglib
		Object target = AopProxyUtil.getPrimitiveProxyTarget(bean);
		assertThat(target, notNullValue());

		// jdkDynamicProxy Ҳ����ˣ�
		// ��Ҫ�޸�CacheManagerHandle.setProxyTargetClass(false);
	}
}
