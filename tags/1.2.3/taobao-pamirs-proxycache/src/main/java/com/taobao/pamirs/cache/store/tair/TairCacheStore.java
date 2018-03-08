package com.taobao.pamirs.cache.store.tair;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.pamirs.cache.store.Store;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;
import com.taobao.tair.TairManager;

/**
 * TairStore 采用淘宝 Tair 的统一缓存存储方案.
 * 通过 Key-Value 的形式将序列化后的对象存入 Tair 服务器中.
 * 
 * 只能采用  PUT , GET , REMOVE 这三种 Key 操作.
 * 不能使用 CLEAR , CLEAN 这种范围清除操作.
 * 
 * 使用该 Store . 数据量可以比较大.  5G ~ 10G
 * 但是必须是固定数据. 数据不会发生变化.
 * 
 * 例如历史订单数据.
 * 
 * 对于配置类信息. 例如 产品配置信息. 
 * 如果采用该 Store.
 * 需要采用判断缓存数据是否超时. 如果超时. 则返回 Null
 * 相当于缓存没有命中. 
 * 
 * 然后外部 Cache 会从数据库获取数据. 然后存入该 Store .
 * 覆盖以上的过期数据.
 * 
 * 
 * **/
public class TairCacheStore<K,V> implements Store<K,V>{
	
	private static final Log logger = LogFactory.getLog(TairCacheStore.class);
	
	/* 缓存管理器 */
	private TairManager tairManager;
	private String cacheCode;

	/* 存储区域 */
	private String region;
	private int namespace;	
    
	/* 过期时间 1天 */
	private int expire = 0;
	
	public TairCacheStore(String cacheCode, String region, int namespace){
		this.cacheCode = cacheCode;
		this.region = region;
		this.namespace = namespace;
		this.tairManager = TairStoreFactory.getTairManager();
	}
    
	private String getTairKey(Object key){
		StringBuilder cacheKey = new StringBuilder();
		cacheKey.append(region)
				.append(cacheCode)
				.append(key);
		return cacheKey.toString();
	}
		
	@SuppressWarnings("unchecked")
	public V get(K key) {
		
		Object returnValue = null;
		Result<DataEntry> result = tairManager.get(namespace, getTairKey(key));
		if (result.isSuccess()) {
			DataEntry data = result.getValue();// 第一个getValue返回DataEntry对象												
			if (data != null){
				returnValue = data.getValue();// 第二个getValue返回真正的value
			}
		}
		if (returnValue == null)
			logger.warn("Tair Cache failed to get object [namespace="
					+ namespace + ", key=" + getTairKey(key)
					+ "]. Error Message : " + result.getRc().getMessage());
		return (V) returnValue;
	}
	
	 
	@SuppressWarnings("deprecation")
	public void remove(K key) {
		// 这里采用失效 是因为 收费线系统将 会有在不同的集群中（容灾）
		ResultCode rc = tairManager.invalid(namespace, getTairKey(key));
		if (!ResultCode.SUCCESS.equals(rc)) {
			logger.error("Tair Cache failed to invalid object [namespace="
					+ namespace + ", key=" + getTairKey(key)
					+ "]. Error Message : " + rc.getMessage());
		}
	}
	public void put(K key, V value) {
		//put前需要remove
		remove(key);
		ResultCode rc = tairManager.put(namespace, getTairKey(key),
				(Serializable) value, 0, expire);
		
		if (!ResultCode.SUCCESS.equals(rc)) {
			logger.error("Tair Cache failed to put object [namespace="
					+ namespace + ", key=" + getTairKey(key) + ", value="
					+ value + "]. Error Message : " + rc.getMessage());
		}
	}
	
	public void replaceData(Map<K, V> newData) {
		throw new RuntimeException("Tair存储 不支持此方法");
	}

	public void clear() {
		throw new RuntimeException("Tair存储 不支持此方法");
	}

	public int size() {		
		throw new RuntimeException("Tair存储 不支持此方法");
	}
}
