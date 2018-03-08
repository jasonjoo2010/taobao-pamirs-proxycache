package com.taobao.pamirs.cache.util;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taobao.pamirs.cache.load.testbean.ASerivce;

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

	@Test
	public void testInnerMethod() {
		ASerivce aSerivce = (ASerivce) context.getBean("aService");
		assertThat(aSerivce, notNullValue());

		aSerivce.testInner(false);// ��һ�β��߻���
		aSerivce.testInner(true);// ��һ�β��߻���
		aSerivce.testInner(false);// �ڶ���Ҳ���߻���
		aSerivce.testInner(true);// �ڶ����߻��棬�ޣ�
	}
}
