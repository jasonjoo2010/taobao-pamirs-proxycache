package com.taobao.pamirs.cache.store;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ·��������cacheproxy���湤�ߣ����ڱ��ط����л���
 * �˴������cahceproxy 1.x�汾�����⴦����Ʒ���湤���������ȥ��
 * @author tiebi.hlw
 *
 * @param <K>
 * @param <V>
 */
public class StoreObject<K,V> implements Serializable{

	private static final long serialVersionUID = 2186360043715004471L;
	
	AtomicLong timestamp = new AtomicLong(System.currentTimeMillis());	
	V value ;
	
	public StoreObject(V value){
		this.value = value;
	}
	public long getTimestamp(){
		return timestamp.get();
	}
	public V getObject(){
		return this.value;
	}
}
