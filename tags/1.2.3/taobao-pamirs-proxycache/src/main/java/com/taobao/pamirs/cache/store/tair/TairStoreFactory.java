package com.taobao.pamirs.cache.store.tair;

import com.taobao.pamirs.cache.ApplicationContextUtil;
import com.taobao.tair.TairManager;

public class TairStoreFactory {
	
	private static String tairManagerName = "tairManager" ;
	
	/**  Tair �洢������  tairManager 
	 * 	 ��ʵ��Ӧ�������ﱻע��.
	 * 
	 *   ��Ҫ���� TairFactory ��ע�� tairManager
	 *   �� Tair Store ��ʹ��.
	 * 	 
	 * **/
	
	public static TairManager getTairManager(){
		return (TairManager)ApplicationContextUtil.getBean(tairManagerName);
	}
	
}
