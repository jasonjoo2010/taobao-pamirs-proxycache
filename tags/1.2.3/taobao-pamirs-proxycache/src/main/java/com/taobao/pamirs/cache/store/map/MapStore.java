/**
 * 
 */
package com.taobao.pamirs.cache.store.map;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.taobao.pamirs.cache.store.Store;


/**
 * MapStore 使用本地 Map 作为 CacheManage 的缓存存储方案.
 * 通过 Key-Value 的形式将对象存入 本地内存中.
 * 
 * 可以采用  PUT , GET , REMOVE 这三种 Key 操作.
 * 可以采用 CLEAR , CLEAN 这种范围清除操作.
 *
 * 通过 MQ TOPIC 进行缓存间的数据通信.接收消息.从而更新缓存 
 *
 * 使用该 Store . 数据量较小.  0 ~ 1G
 * 访问耗时极低. 
 * 适用于数据量小但变化较多的场合.
 * 
 * 例如基础型数据.
 * 
 * **/
public class MapStore<K, V> implements Store<K, V> {
	
//	private static final Log logger = LogFactory.getLog(MapStore.class);

	private final ConcurrentHashMap<K, V> datas = new ConcurrentHashMap<K, V>();
    
	public V get(K key) {
		return datas.get(key);
	}

	public void put(K key, V value) {
		datas.put(key, value);
	}

	public void putAll(Map<K, V> map) {
		datas.putAll(map);
	}	
	
	public void remove(K key) {
		datas.remove(key);
	}

	public void clear() {
		this.datas.clear();
	}

	public void replaceData(Map<K, V> map) {
		this.datas.clear();
		this.datas.putAll(map);
	}

	public int size() {
		return this.datas.size();
	}
}
