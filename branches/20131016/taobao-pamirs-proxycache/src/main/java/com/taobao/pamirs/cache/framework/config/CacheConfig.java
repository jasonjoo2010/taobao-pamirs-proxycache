package com.taobao.pamirs.cache.framework.config;

import com.taobao.pamirs.cache.load.verify.Verfication;
import com.taobao.pamirs.cache.store.StoreType;

/**
 * ����������
 * 
 * @author xiaocheng 2012-11-2
 */
public class CacheConfig extends CacheModule {

	//
	private static final long serialVersionUID = 8164876688008497503L;

	/**
	 * ��������
	 * 
	 * @see StoreType
	 */
	@Verfication(name = "��������", notEmpty = true, isStoreType = true)
	private String storeType;

	/**
	 * Map�Զ�������ʽ����ѡ��(just for map��
	 * 
	 * @see StoreType.MAP
	 */
	private String storeMapCleanTime;

	/**
	 * �����������ѡ��
	 */
	private String storeRegion;

	/**
	 * Tair�����ռ䣨just for tair��
	 * 
	 * @see StoreType.TAIR
	 */
	@Verfication(name = "Tair�����ռ�", notNull = true, when = { StoreType.TAIR })
	private Integer storeTairNameSpace;
	
	/**
	 * �Ƿ�ͳ������
	 */
	private boolean statisCount =false;

	public String getStoreType() {
		return storeType;
	}

	public void setStoreType(String storeType) {
		this.storeType = storeType;
	}

	public String getStoreMapCleanTime() {
		return storeMapCleanTime;
	}

	public void setStoreMapCleanTime(String storeMapCleanTime) {
		this.storeMapCleanTime = storeMapCleanTime;
	}

	public String getStoreRegion() {
		return storeRegion;
	}

	public void setStoreRegion(String storeRegion) {
		this.storeRegion = storeRegion;
	}

	public Integer getStoreTairNameSpace() {
		return storeTairNameSpace;
	}

	public void setStoreTairNameSpace(Integer storeTairNameSpace) {
		this.storeTairNameSpace = storeTairNameSpace;
	}

	public boolean isStatisCount() {
		return statisCount;
	}

	public void setStatisCount(boolean statisCount) {
		this.statisCount = statisCount;
	}

}
