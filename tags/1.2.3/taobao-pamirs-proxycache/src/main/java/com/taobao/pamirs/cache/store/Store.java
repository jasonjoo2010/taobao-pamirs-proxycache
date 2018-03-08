/**
 * 
 */
package com.taobao.pamirs.cache.store;

import java.util.Map;

/**
 * 
 * ���ݴ洢�ӿ�
 * @author xuanyu
 * 
 */
public interface Store<K, V> {
	
	public static String STORE_TYPE_MAP ="map";
	public static String STORE_TYPE_TAIR ="tair";
	
	/**
	 * ����װ�����е�����
	 * @param newData
	 */
	public void replaceData(Map<K,V> newData);
	/**
	 * ��ȡ����
	 * @param key
	 * @return
	 */
	public V get(K key);
	/**
	 * ��������
	 * @param key
	 * @param value
	 * @return
	 */
	public void put(K key, V value);
	/**
	 * ɾ������
	 * @param key
	 * @return
	 */
	public void remove(K key);
	/**
	 * ������е�����
	 */
	public void clear();
	/**
	 * ��ȡ����������
	 */
	public int size();
	
}
