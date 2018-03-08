/**
 * 
 */
package com.taobao.pamirs.cache.framework;

import java.io.Serializable;

/**
 * 缓存支持接口
 * 
 * @author xuanyu
 * @author xiaocheng 2012-10-30
 */
public interface ICache<K extends Serializable, V extends Serializable> {

	/**
	 * 获取数据
	 * 
	 * @param key
	 * @return
	 */
	public V get(K key);

	
	/**
	 * 设置数据，如果数据已经存在且版本一致（不存在版本号肯定正确），则覆盖
	 * 
	 * @param key
	 * @param value
	 * @param expireTime 数据的有效时间（绝对时间），单位毫秒
	 * @param version 数据版本号 (tair支持)
	 *            
	 */
	public void put(K key, V value,int version,int expireTime) ;
	
	

	/**
	 * 删除key对应的数据
	 * 
	 * @param key
	 */
	public void remove(K key);

	/**
	 * 清除所有的数据
	 */
	public void clear();

	/**
	 * 获取缓存数据量
	 * 
	 * @return
	 */
	public int size();
	
	
	/**
	 * 隐藏数据
	 * @param key
	 */
	public void hidden(K key);
	
	
	/**
	 * 获取数据版本号
	 * @param key
	 * @param removeMode
	 * @return
	 */
	public Integer getDataVersion(K key,String removeMode);

}
