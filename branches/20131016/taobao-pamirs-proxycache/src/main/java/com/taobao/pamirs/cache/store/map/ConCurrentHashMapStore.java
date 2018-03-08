/**
 * 
 */
package com.taobao.pamirs.cache.store.map;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.taobao.pamirs.cache.framework.ICache;

/**
 * @author xin
 * 
 */
public class ConCurrentHashMapStore<K extends Serializable, V extends Serializable>
		implements ICache<K, V> {

	private final Map<K, ObjectBoxing<V>> cache = new ConcurrentHashMap<K, ObjectBoxing<V>>();

	@Override
	public V get(K key) {
		ObjectBoxing<V> cacheItem = cache.get(key);
		if (null == cacheItem) {
			return null;
		} else {
			return cacheItem.getObject();
		}
	}

	@Override
	public void put(K key, V value) {
		cache.remove(key);
		cache.put(key, new ObjectBoxing<V>(value, null));
	}

	@Override
	public void put(K key, V value, int expireTime) {
		cache.remove(key);
		cache.put(key, new ObjectBoxing<V>(value, expireTime));
	}

	@Override
	public void remove(K key) {
		cache.remove(key);
	}

	@Override
	public void clear() {
		cache.clear();
	}

	@Override
	public int size() {
		return cache.size();
	}

}
