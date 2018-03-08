package com.taobao.pamirs.cache.store;

/**
 * ª∫¥Ê¥Ê¥¢¿‡–Õ
 * 
 * @author xiaocheng 2012-11-1
 */
public enum StoreType {

	CONCURRENTMAP("cmap"),RULMAP("map"), TAIR("tair");

	private String name;

	private StoreType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static StoreType toEnum(String name) {
		if (RULMAP.getName().equals(name))
			return RULMAP;
		if (TAIR.getName().equals(name))
			return TAIR;
		if (CONCURRENTMAP.getName().equals(name))
			return CONCURRENTMAP;
		return null;
	}

}
