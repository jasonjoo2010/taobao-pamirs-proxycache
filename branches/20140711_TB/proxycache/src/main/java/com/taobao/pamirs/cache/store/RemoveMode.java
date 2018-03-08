package com.taobao.pamirs.cache.store;

/**
 * ����ģʽ
 * @author tiebi.hlw
 *
 */
public enum RemoveMode {

	/**
	 * ɾ������
	 */
	INVAILD("invaild"), 
	
	/**
	 * ��ɾ�����ݣ�get��ȡ����
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
