package com.taobao.pamirs.cache.framework.aop.advisor;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;

import com.taobao.pamirs.cache.CacheManager;
import com.taobao.pamirs.cache.framework.aop.advice.AnnotationCacheManagerRoundAdvice;
import com.taobao.pamirs.cache.framework.aop.advice.CacheManagerRoundAdvice;
import com.taobao.pamirs.cache.load.impl.AnnotationConfigCacheManager;

/**
 * π€≤Ï’ﬂ
 * 
 * @author xuannan
 * @author xiaocheng 2012-10-30
 */
public class CacheManagerAdvisor implements Advisor {
	
	private CacheManagerRoundAdvice advice;

	public CacheManagerAdvisor(CacheManager cacheManager, String beanName) {
		if (cacheManager instanceof AnnotationConfigCacheManager) {
			this.advice = new AnnotationCacheManagerRoundAdvice(cacheManager, beanName);
		} else {			
			this.advice = new CacheManagerRoundAdvice(cacheManager, beanName);
		}
	}

	public Advice getAdvice() {
		return advice;
	}

	public boolean isPerInstance() {
		return false;
	}
}
