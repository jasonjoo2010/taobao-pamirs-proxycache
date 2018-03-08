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
 * 缓存管理处理类
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
		
		//装载 bean 的时候进行缓存代理功能设置.
		if (this.cacheManager.getCacheBeanNameSet().contains(beanName)) {	
			log.info("CacheManager ProxyBean:" + beanName);
			return new CacheManagerAdvisor[] { 
					new CacheManagerAdvisor(this.cacheManager,beanName) 
					};
		//这个判断是为将来功能扩展做准备.如果符合其他的切面规则.则采取不同的代理.	
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



