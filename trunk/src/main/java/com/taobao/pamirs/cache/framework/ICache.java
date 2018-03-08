/**
 * 
 */
package com.taobao.pamirs.cache.framework;

import java.io.Serializable;

/**
 * ����֧�ֽӿ�
 * 
 * @author xuanyu
 * @author xiaocheng 2012-10-30
 */
public interface ICache<K extends Serializable, V extends Serializable> {

	/**
	 * ��ȡ����
	 * 
	 * @param key
	 * @return
	 */
	public V get(K key);

	/**
	 * �������ݣ���������Ѿ����ڣ��򸲸ǣ���������ڣ�������
	 * 
	 * @param key
	 * @param value
	 */
	public void put(K key, V value);

	/**
	 * �������ݣ���������Ѿ����ڣ��򸲸ǣ���������ڣ�������
	 * 
	 * @param key
	 * @param value
	 * @param expireTime
	 *            ���ݵ���Чʱ�䣨����ʱ�䣩����λ����
	 */
	public void put(K key, V value, int expireTime);

	/**
	 * ɾ��key��Ӧ������
	 * 
	 * @param key
	 */
	public void remove(K key);
	
	/**
	 * ������е����ݡ����LocalMap��
	 */
	public void clear();
	
	/**
	 * ʧЧ֮ǰ�洢���桾���Tair��
	 */
	public void invalidBefore();

	/**
	 * ��ȡ����������
	 * 
	 * @return
	 */
	public int size();
}
