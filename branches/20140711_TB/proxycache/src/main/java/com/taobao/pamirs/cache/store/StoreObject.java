package com.taobao.pamirs.cache.store;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 路径拷贝至cacheproxy缓存工具，用于本地反序列化，
 * 此处是针对cahceproxy 1.x版本的特殊处理，商品缓存工具升级后可去除
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
