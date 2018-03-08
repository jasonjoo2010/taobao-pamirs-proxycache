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
	 * ��������������
	 * ������proxy����ԭ���������ڱ仯�������ã���ԭ�Ӳ������������ù��̿��ܶ�ȡ�����ϲ��������,
	 *   ��ͨ��notCacheWhenReload����ִ���滻˲�䲻�߻��桿
	 * @return
	 */
	boolean runtimeReloadConfig(CacheConfig newConfig);
	

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
