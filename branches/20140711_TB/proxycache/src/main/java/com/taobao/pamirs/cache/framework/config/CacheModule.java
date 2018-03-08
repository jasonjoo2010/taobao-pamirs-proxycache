package com.taobao.pamirs.cache.framework.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * �������ģ��--��load��
 * 
 * @author poxiao.gj
 * @author xiaocheng 2012-11-19
 */
public class CacheModule implements Serializable {
	
	
	/**
	 * ��̬���ػ�������ʱ���߻���
	 */
	private boolean notCacheWhenReload=false; 

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1194734355755304229L;

	/**
	 * ����bean����
	 */
	private List<CacheBean> cacheBeans;

	/**
	 * ������bean����
	 */
	private List<CacheCleanBean> cacheCleanBeans;

	public List<CacheBean> getCacheBeans() {
		if (cacheBeans == null)
			cacheBeans = new ArrayList<CacheBean>();

		return cacheBeans;
	}

	public void setCacheBeans(List<CacheBean> cacheBeans) {
		this.cacheBeans = cacheBeans;
	}

	public List<CacheCleanBean> getCacheCleanBeans() {
		if (cacheCleanBeans == null)
			cacheCleanBeans = new ArrayList<CacheCleanBean>();

		return cacheCleanBeans;
	}

	public void setCacheCleanBeans(List<CacheCleanBean> cacheCleanBeans) {
		this.cacheCleanBeans = cacheCleanBeans;
	}

	public boolean isNotCacheWhenReload() {
		return notCacheWhenReload;
	}

	public void setNotCacheWhenReload(boolean notCacheWhenReload) {
		this.notCacheWhenReload = notCacheWhenReload;
	}
	

}
