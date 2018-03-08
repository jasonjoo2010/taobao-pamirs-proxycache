/**
 * 
 */
package com.taobao.pamirs.cache.store;

import java.util.Map;

/**
 * 
 * 数据存储接口
 * @author xuanyu
 * 
 */
public interface Store<K, V> {
	
	public static String STORE_TYPE_MAP ="map";
	public static String STORE_TYPE_TAIR ="tair";
	
	/**
	 * 重新装载所有的数据
	 * @param newData
	 */
	public void replaceData(Map<K,V> newData);
	/**
	 * 获取数据
	 * @param key
	 * @return
	 */
	public V get(K key);
	/**
	 * 更新数据
	 * @param key
	 * @param value
	 * @return
	 */
	public void put(K key, V value);
	/**
	 * 删除数据
	 * @param key
	 * @return
	 */
	public void remove(K key);
	/**
	 * 清除所有的数据
	 */
	public void clear();
	/**
	 * 获取缓存数据量
	 */
	public int size();
	
}
