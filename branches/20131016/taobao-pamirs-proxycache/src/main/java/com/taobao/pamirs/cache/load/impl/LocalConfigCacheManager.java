package com.taobao.pamirs.cache.load.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.taobao.pamirs.cache.extend.jmx.annotation.JmxClass;
import com.taobao.pamirs.cache.framework.config.CacheConfig;
import com.taobao.pamirs.cache.framework.config.CacheModule;
import com.taobao.pamirs.cache.load.AbstractCacheConfigService;
import com.taobao.pamirs.cache.load.LoadConfigException;
import com.taobao.pamirs.cache.util.ConfigUtil;

/**
 * ���ؼ��ػ������÷���
 * 
 * @author poxiao.gj
 * @date 2012-11-13
 */
@JmxClass
public class LocalConfigCacheManager extends AbstractCacheConfigService {

	private List<String> configFilePaths;

	public void setConfigFilePaths(List<String> configFilePaths) {
		this.configFilePaths = configFilePaths;
	}

	/**
	 * ���ؼ��ػ�������
	 * 
	 * @return
	 */
	public CacheConfig loadConfig() {
		List<CacheModule> cacheModules = getCacheModules();
		if (cacheModules.size() <= 0) {
			throw new LoadConfigException("�Ƿ��Ļ������ã�CacheModule�б�Ϊ��");
		}

		CacheConfig cacheConfig = new CacheConfig();
		cacheConfig.setStoreType(getStoreType());
		cacheConfig.setStoreMapCleanTime(getMapCleanTime());
		cacheConfig.setStoreRegion(getStoreRegion());
		cacheConfig.setStoreTairNameSpace(getTairNameSpace());
		for (CacheModule cacheModule : cacheModules) {
			cacheConfig.getCacheBeans().addAll(cacheModule.getCacheBeans());
			cacheConfig.getCacheCleanBeans().addAll(
					cacheModule.getCacheCleanBeans());
		}

		return cacheConfig;
	}

	/**
	 * ���ļ��л�ȡ�����ļ���Ϣ
	 * 
	 * @return
	 * @throws Exception
	 */
	private List<CacheModule> getCacheModules() {
		if (configFilePaths == null || configFilePaths.size() <= 0) {
			throw new IllegalArgumentException("�Ƿ������ļ�·���Ĳ����������ļ��б���Ϊ��");
		}

		InputStream input = null;
		try {
			ClassLoader classLoader = Thread.class.getClassLoader();
			if (classLoader == null) {
				classLoader = LocalConfigCacheManager.class.getClassLoader();
			}
			List<CacheModule> cacheModuleList = new ArrayList<CacheModule>();
			for (String configFilePath : configFilePaths) {
				input = classLoader.getResourceAsStream(configFilePath);
				if (input != null) {
					CacheModule cacheModule = ConfigUtil
							.getCacheConfigModule(input);
					cacheModuleList.add(cacheModule);
				}
			}
			return cacheModuleList;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
