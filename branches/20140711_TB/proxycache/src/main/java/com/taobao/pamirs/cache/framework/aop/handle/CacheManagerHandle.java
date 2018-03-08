package com.taobao.pamirs.cache.framework.aop.handle;

import org.apache.commons.logging.Log;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.BeansException;

import com.taobao.pamirs.cache.CacheManager;
import com.taobao.pamirs.cache.framework.aop.advisor.CacheManagerAdvisor;
import com.taobao.pamirs.cache.util.CaCheProxyLog;
import com.taobao.pamirs.cache.util.ConfigUtil;

/**
 * �����������
 * 
 * @author xuannan
 */
@SuppressWarnings("serial")
public class CacheManagerHandle extends AbstractAutoProxyCreator {

	private static final Log log =  CaCheProxyLog.LOGGER_DEFAULT;

	private CacheManager cacheManager;

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public CacheManagerHandle() {
		this.setProxyTargetClass(true);
	}

	@SuppressWarnings("rawtypes")
	protected Object[] getAdvicesAndAdvisorsForBean(Class beanClass,
			String beanName, TargetSource targetSource) throws BeansException {
		
		log.debug("CacheManagerHandle in:" + beanName);
		
		if (ConfigUtil.isBeanHaveCache(cacheManager.getCacheConfig(), beanName)) {

			log.warn("CacheManager start... ProxyBean:" + beanName);

			return new CacheManagerAdvisor[] { new CacheManagerAdvisor(
					cacheManager, beanName) };
		}

		return DO_NOT_PROXY;
	}

}
