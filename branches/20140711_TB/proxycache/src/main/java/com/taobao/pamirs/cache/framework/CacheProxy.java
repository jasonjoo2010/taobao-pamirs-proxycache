package com.taobao.pamirs.cache.framework;

import static com.taobao.pamirs.cache.framework.listener.CacheOprator.GET;
import static com.taobao.pamirs.cache.framework.listener.CacheOprator.PUT;
import static com.taobao.pamirs.cache.framework.listener.CacheOprator.REMOVE;

import java.io.Serializable;

import org.apache.commons.logging.Log;

import com.taobao.pamirs.cache.framework.config.MethodConfig;
import com.taobao.pamirs.cache.framework.listener.CacheObservable;
import com.taobao.pamirs.cache.framework.listener.CacheOprateInfo;
import com.taobao.pamirs.cache.store.RemoveMode;
import com.taobao.pamirs.cache.store.StoreType;
import com.taobao.pamirs.cache.util.CaCheProxyLog;

/**
 * 缓存处理适配器
 * 
 * @author xiaocheng 2012-10-31
 */
public class CacheProxy<K extends Serializable, V extends Serializable> extends
		CacheObservable {
	
	private static final Log log = CaCheProxyLog.LOGGER_DEFAULT;


	private StoreType storeType;
	private String storeRegion;
	private String key;

	/** 注入真正的cache实现 */
	private ICache<K, V> cache;

	private String beanName;
	private MethodConfig methodConfig;
	
	private boolean isNotifyListeners;


	public CacheProxy(StoreType storeType, String storeRegion, String key,
			ICache<K, V> cache, String beanName,
			MethodConfig methodConfig,boolean isNotifyListeners) {
		this.storeType = storeType;
		this.storeRegion = storeRegion;
		this.key = key;
		this.cache = cache;
		this.beanName = beanName;
		this.methodConfig = methodConfig;
		this.isNotifyListeners=isNotifyListeners;
	}

	public V get(K key, String ip) {
		if (!isUseCache)
			return null;

		CacheException cacheException = null;
		V v = null;

		long start = System.currentTimeMillis();
		try {
			v = cache.get(key);
		} catch (CacheException e) {
			cacheException = e;
			log.error("cache proxy get error,key="+key+",errCode="+e.getErrCode()+",errMsg="+e.getErrMsg(), e);
		}

		// listener
		if(isNotifyListeners){
			
			long end = System.currentTimeMillis();

			boolean isHitting = v != null;// 是否命中，null即未命中
			
			notifyListeners(GET, new CacheOprateInfo(key, end - start, isHitting,
					beanName, methodConfig, cacheException, ip));
		}
		if(v==null){
			log.warn("cache proxy get object return null,key="+key);
		}

		return v;
	}

	public void put(K key, V value,String ip) {

		CacheException cacheException = null;

		long start = System.currentTimeMillis();
		try {
			int version=0;
			int expireTime=0;
			if(methodConfig.isUseVersion()){
				version=cache.getDataVersion(key, methodConfig.getRemoveMode());
			}
			if(methodConfig.getExpiredTime()!=null){
				expireTime=methodConfig.getExpiredTime();
			}
			cache.put(key, value, version, expireTime);
		} catch (CacheException e) {
			cacheException = e;
			log.error("cache proxy put error,key="+key+",errCode="+e.getErrCode()+",errMsg="+e.getErrMsg(), e);
		}

		// listener
		if(isNotifyListeners){
			long end = System.currentTimeMillis();
			notifyListeners(PUT, new CacheOprateInfo(key, end - start, true,
					beanName, methodConfig, cacheException, ip));
		}
		
	}
	
	
	public void remove(K key, String ip) {
		CacheException cacheException = null;

		long start = System.currentTimeMillis();
		try {
			if(RemoveMode.HIDDEN.getName().equals(methodConfig.getRemoveMode())){
				cache.hidden(key);
			}else{
				cache.remove(key);
			}
		} catch (CacheException e) {
			cacheException = e;
			log.error("cache proxy remove error,key="+key+",errCode="+e.getErrCode()+",errMsg="+e.getErrMsg(), e);
		}

		// listener
		if(isNotifyListeners){
			
			long end = System.currentTimeMillis();
			
			notifyListeners(REMOVE, new CacheOprateInfo(key, end - start, true,
					beanName, methodConfig, cacheException, ip));
		}
	
	}
	
	

	public void clear() {
		cache.clear();
	}

	public int size() {
		return cache.size();
	}

	/** 单个方法的缓存开关 */
	private boolean isUseCache = true;

	public void setIsUseCache(boolean isUseCache) {
		this.isUseCache = isUseCache;
	}

	public boolean isUseCache() {
		return isUseCache;
	}

	public void setUseCache(boolean isUseCache) {
		this.isUseCache = isUseCache;
	}

	public StoreType getStoreType() {
		return storeType;
	}

	public String getStoreRegion() {
		return storeRegion;
	}

	public String getKey() {
		return key;
	}

	public String getBeanName() {
		return beanName;
	}

	public MethodConfig getMethodConfig() {
		return methodConfig;
	}

}
