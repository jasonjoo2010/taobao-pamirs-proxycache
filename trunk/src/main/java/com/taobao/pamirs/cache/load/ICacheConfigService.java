package com.taobao.pamirs.cache.load;

import com.taobao.pamirs.cache.framework.config.CacheConfig;

/**
 * �������û�ȡ�ӿ�
 * 
 * @author xiaocheng 2012-11-12
 */
public interface ICacheConfigService {

	/**
	 * ���ػ�������
	 * 
	 * @return
	 */
	CacheConfig loadConfig();

	/**
	 * �Զ�����Ĭ������
	 * 
	 * @param cacheConfig
	 */
	void autoFillCacheConfig(CacheConfig cacheConfig);

	/**
	 * У�����úϷ���
	 * 
	 * @param cacheConfig
	 */
	void verifyCacheConfig(CacheConfig cacheConfig);
}
