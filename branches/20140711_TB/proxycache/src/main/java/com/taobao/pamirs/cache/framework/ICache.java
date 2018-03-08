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
	 * �������ݣ���������Ѿ������Ұ汾һ�£������ڰ汾�ſ϶���ȷ�����򸲸�
	 * 
	 * @param key
	 * @param value
	 * @param expireTime ���ݵ���Чʱ�䣨����ʱ�䣩����λ����
	 * @param version ���ݰ汾�� (tair֧��)
	 *            
	 */
	public void put(K key, V value,int version,int expireTime) ;
	
	

	/**
	 * ɾ��key��Ӧ������
	 * 
	 * @param key
	 */
	public void remove(K key);

	/**
	 * ������е�����
	 */
	public void clear();

	/**
	 * ��ȡ����������
	 * 
	 * @return
	 */
	public int size();
	
	
	/**
	 * ��������
	 * @param key
	 */
	public void hidden(K key);
	
	
	/**
	 * ��ȡ���ݰ汾��
	 * @param key
	 * @param removeMode
	 * @return
	 */
	public Integer getDataVersion(K key,String removeMode);

}
