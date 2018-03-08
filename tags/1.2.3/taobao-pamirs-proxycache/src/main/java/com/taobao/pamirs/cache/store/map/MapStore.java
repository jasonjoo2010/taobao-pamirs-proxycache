/**
 * 
 */
package com.taobao.pamirs.cache.store.map;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.taobao.pamirs.cache.store.Store;


/**
 * MapStore ʹ�ñ��� Map ��Ϊ CacheManage �Ļ���洢����.
 * ͨ�� Key-Value ����ʽ��������� �����ڴ���.
 * 
 * ���Բ���  PUT , GET , REMOVE ������ Key ����.
 * ���Բ��� CLEAR , CLEAN ���ַ�Χ�������.
 *
 * ͨ�� MQ TOPIC ���л���������ͨ��.������Ϣ.�Ӷ����»��� 
 *
 * ʹ�ø� Store . ��������С.  0 ~ 1G
 * ���ʺ�ʱ����. 
 * ������������С���仯�϶�ĳ���.
 * 
 * �������������.
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
