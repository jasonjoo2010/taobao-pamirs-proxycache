package com.taobao.pamirs.cache.store;

/**
 * 清理模式
 * @author tiebi.hlw
 *
 */
public enum RemoveMode {

	/**
	 * 删除数据
	 */
	INVAILD("invaild"), 
	
	/**
	 * 不删除数据，get获取不到
	 */
	HIDDEN("hidden");
	
	private String name;

	private RemoveMode(String name) {
		this.name = name;
	}
	
	
	public String getName() {
		return name;
	}

	public static RemoveMode toEnum(String name) {
		if (INVAILD.getName().equals(name))
			return INVAILD;

		if (HIDDEN.getName().equals(name))
			return HIDDEN;

		return null;
	}
	
	public boolean equals(RemoveMode mode){
		if(mode==null||mode.getName()==null){
			return false;
		}
		if(this.getName()==null){
			return false;
		}
		return this.getName().equals(mode.getName());
	}

	
}
