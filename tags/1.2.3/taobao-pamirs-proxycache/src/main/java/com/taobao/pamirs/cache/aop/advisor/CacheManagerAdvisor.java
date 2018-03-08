package com.taobao.pamirs.cache.aop.advisor;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;

import com.taobao.pamirs.cache.aop.advice.CacheManagerRoundAdvice;
import com.taobao.pamirs.cache.manager.CacheManager;

public class CacheManagerAdvisor implements Advisor {
	CacheManagerRoundAdvice advice;
    public CacheManagerAdvisor(CacheManager cacheManager,String beanName){
    	this.advice = new CacheManagerRoundAdvice(cacheManager,beanName);
    }
	public Advice getAdvice() {
		return advice;
	}
	public boolean isPerInstance() {
		return false;
	}
}
