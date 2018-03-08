package com.taobao.pamirs.cache.store;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

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
