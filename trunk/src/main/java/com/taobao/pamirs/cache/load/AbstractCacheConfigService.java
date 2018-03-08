package com.taobao.pamirs.cache.load;

import com.taobao.pamirs.cache.CacheManager;
import com.taobao.pamirs.cache.store.StoreType;

/**
 * ������󹫹�
 * 
 * @author poxiao.gj
 * @date 2012-11-13
 */
public abstract class AbstractCacheConfigService extends CacheManager {

	/**
	 * ����洢����
	 * 
	 * @see StoreType
	 */
	private String storeType;

	/**
	 * tair����ռ�
	 * 
	 * @see StoreType.TAIR
	 */
	private Integer tairNameSpace;

	/**
	 * ���ػ�������ʱ��
	 * 
	 * @see StoreType.MAP
	 */
	private String mapCleanTime;

	/**
	 * ���滷������
	 */
	private String storeRegion;

	public String getStoreType() {
		return storeType;
	}

	public Integer getTairNameSpace() {
		return tairNameSpace;
	}

	public String getMapCleanTime() {
		return mapCleanTime;
	}

	public String getStoreRegion() {
		return storeRegion;
	}

	public void setStoreType(String storeType) {
		this.storeType = storeType;
	}

	public void setTairNameSpace(Integer tairNameSpace) {
		this.tairNameSpace = tairNameSpace;
	}

	public void setMapCleanTime(String mapCleanTime) {
		this.mapCleanTime = mapCleanTime;
	}

	public void setStoreRegion(String storeRegion) {
		this.storeRegion = storeRegion;
	}

}
