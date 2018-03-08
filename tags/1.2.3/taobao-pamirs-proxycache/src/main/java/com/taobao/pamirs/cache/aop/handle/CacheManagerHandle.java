package com.taobao.pamirs.cache.aop.handle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.taobao.pamirs.cache.aop.advisor.CacheManagerAdvisor;
import com.taobao.pamirs.cache.manager.CacheManager;

/**
 * �����������
 * 
 * @author xuannan
 * 
 */
@SuppressWarnings("serial")
public class CacheManagerHandle extends AbstractAutoProxyCreator implements ApplicationListener {
	private static transient Log log = LogFactory.getLog(CacheManagerHandle.class);
		
	CacheManager cacheManager;	
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public CacheManagerHandle(){
		this.setProxyTargetClass(true);
	}
	
	@SuppressWarnings("rawtypes")
	protected Object[] getAdvicesAndAdvisorsForBean(Class beanClass,
			String beanName, TargetSource targetSource) throws BeansException {
		
		//װ�� bean ��ʱ����л������������.
		if (this.cacheManager.getCacheBeanNameSet().contains(beanName)) {	
			log.info("CacheManager ProxyBean:" + beanName);
			return new CacheManagerAdvisor[] { 
					new CacheManagerAdvisor(this.cacheManager,beanName) 
					};
		//����ж���Ϊ����������չ��׼��.��������������������.���ȡ��ͬ�Ĵ���.	
		}else {
			return DO_NOT_PROXY;
		}
	}

	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent) {
			//
		}
	}
}



