/**
 * 
 */
package com.taobao.pamirs.cache.store.map;

import java.io.Serializable;

import com.taobao.pamirs.cache.framework.ICache;
import com.taobao.pamirs.cache.util.lru.ConcurrentLRUCacheMap;

/**
 * MapStore ʹ�ñ��� ConcurrentLRUCacheMap ��Ϊ CacheManage �Ļ���洢����.
 * <p>
 * 
 * <pre>
 * ͨ�� Key-Value ����ʽ��������� �����ڴ���.
 * 
 * ���Բ��� PUT , PUT_EXPIRETIME , GET , REMOVE ������ Key ����. 
 * ���Բ��� CLEAR , CLEAN ���ַ�Χ�������.
 * 
 * ʹ�ø� Store . ��������С. 0 ~ 1G ���ʺ�ʱ����. 
 * ������������С���仯�϶�ĳ���.
 * 
 * �������������.
 * </pre>
 * 
 * @author xuanyu
 * @author xiaocheng 2012-11-2
 */
public class MapStore<K extends Serializable, V extends Serializable>
		implements ICache<K, V> {

	private final ConcurrentLRUCacheMap<K, ObjectBoxing<V>> datas;

	public MapStore() {
		datas = new ConcurrentLRUCacheMap<K, ObjectBoxing<V>>();
	}

	public MapStore(int size, int segmentSize) {
		datas = new ConcurrentLRUCacheMap<K, ObjectBoxing<V>>(size, segmentSize);
	}

	@Override
	public V get(K key) {
		ObjectBoxing<V> storeObject = datas.get(key);
		if (storeObject == null) {
			datas.remove(key);
			return null;
		}

		V v = storeObject.getObject();
		if (v == null)
			datas.remove(key);

		return v;
	}
	
	public void put(K key, V value,int version,int expireTime) {
		if (value == null)
			return;

		ObjectBoxing<V> storeObject = new ObjectBoxing<V>(value, expireTime);
		datas.put(key, storeObject);
	}

	
	
	@Override
	public void remove(K key) {
		datas.remove(key);
	}

	@Override
	public void clear() {
		datas.clear();
	}

	@Override
	public int size() {
		return datas.size();
	}

	@Override
	public void hidden(K key) {
		throw new RuntimeException("Map�洢 ��֧�ִ˷���");
	}

	@Override
	public Integer getDataVersion(K key, String removeMode) {
		// ��֧�ְ汾��
		return null;
	}

}
