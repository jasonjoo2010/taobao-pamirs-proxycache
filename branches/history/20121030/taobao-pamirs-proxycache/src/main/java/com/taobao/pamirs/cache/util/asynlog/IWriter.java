package com.taobao.pamirs.cache.util.asynlog;

/**
 * �첽Log�ӿ�
 * 
 * @author xiaocheng 2012-11-9
 */
public interface IWriter<T> {

	/**
	 * ����д
	 * 
	 * @param content
	 */
	public void write(T content);

}
