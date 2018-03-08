/**
 * 
 */
package com.taobao.pamirs.cache.load.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taobao.pamirs.cache.framework.config.CacheBean;
import com.taobao.pamirs.cache.framework.config.CacheConfig;
import com.taobao.pamirs.cache.framework.config.MethodConfig;
import com.taobao.pamirs.cache.load.AbstractCacheConfigService;
import com.taobao.pamirs.cache.load.LoadConfigException;

/**
 * @author xin
 * 
 *         �����ü��أ�Ŀǰֻ����cacheConfig��cleanConfig�����أ� cacheConfig��ÿ��item��
 *         ֻ����beanName��methodName��storeType��cleanTimeExp��expTime��beRemoteCalled��
 * 
 */
public class SimpleLocalCacheManager extends AbstractCacheConfigService {

	private List<String> doCacheConfig;
	private List<String> cleanConfig;// ĿǰcleanConfig
										// ��������ϵ����⣬����cleanConfig���м��ء�

	private static String SPLITE_SGIN = ";";

	@Override
	public CacheConfig loadConfig() throws Exception {
		if ((getDoCacheConfig() == null || getDoCacheConfig().size() <= 0)
				&& (getCleanConfig() == null || getCleanConfig().size() <= 0)) {
			throw new LoadConfigException("�Ƿ��Ļ������ã�config�б�Ϊ��");
		}
		//�������ó�ʼ��
		CacheConfig cacheConfigItem = new CacheConfig();
		cacheConfigItem.setStoreType(getStoreType());
		cacheConfigItem.setStoreMapCleanTime(getMapCleanTime());
		cacheConfigItem.setStoreRegion(getStoreRegion());
		cacheConfigItem.setStoreTairNameSpace(getTairNameSpace());
		cacheConfigItem.setStatisCount(isStatisCount());
		Map<String, CacheBean> tmp = new HashMap<String, CacheBean>();
		//���ڹ�����г�ʼ��  
		//������ͬbean�µĲ�ͬ�����������ó�ʼ��
		for (String confItem : getDoCacheConfig()) {
			MethodConfig mc = generateCacheBean(confItem);
			if (tmp.get(mc.getBeanName()) == null) {
				CacheBean cb = new CacheBean();
				cb.setBeanName(mc.getBeanName());
				tmp.put(mc.getBeanName(), cb);
			}
			tmp.get(mc.getBeanName()).getCacheMethods().add(mc);
		}
		cacheConfigItem.getCacheBeans().addAll(tmp.values());
		
		return cacheConfigItem;
	}

	/**
	 * beanName��methodName��storeType��cleanTimeExp��expTime��beRemoteCalled
	 * 
	 * 
	 * @return
	 * @throws Exception
	 */
	private MethodConfig generateCacheBean(String confItem) throws Exception {
//		beanName=appConfService;
//		methodName=getAppConfByCodeAndType;
//		expiredTime=3600;
//		storeType=tair
		String[] items = confItem.split(SPLITE_SGIN);
		MethodConfig mconf = new MethodConfig();
		for (String item : items) {
			if (item.split("=").length != 2) {
				throw new Exception("�������ô������⣬ֵΪ" + item);
			}
			String key = item.split("=")[0].trim();
			String value = item.split("=")[1].trim();
			if ("beanName".equals(key)) {
				mconf.setBeanName(value);
			} else if ("methodName".equals(key)) {
				mconf.setMethodName(value);
			} else if ("storeType".equals(key)) {
				mconf.setStoreType(value);
			} else if ("cleanTimeExp".equals(key)) {
				mconf.setCleanTimeExp(value);
			} else if ("expTime".equals(key)) {
				mconf.setExpiredTime(Integer.parseInt(value));
			} else if ("beRemoteCalled".equals(key)) {
				mconf.setBeRemoteCalled(Boolean.parseBoolean(value));
			}
		}

		return mconf;
	}

	public List<String> getDoCacheConfig() {
		return doCacheConfig;
	}

	public void setDoCacheConfig(List<String> doCacheConfig) {
		this.doCacheConfig = doCacheConfig;
	}

	public List<String> getCleanConfig() {
		return cleanConfig;
	}

	public void setCleanConfig(List<String> cleanConfig) {
		this.cleanConfig = cleanConfig;
	}

}
