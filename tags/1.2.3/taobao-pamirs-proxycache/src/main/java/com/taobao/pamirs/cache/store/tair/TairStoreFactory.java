package com.taobao.pamirs.cache.store.tair;

import com.taobao.pamirs.cache.ApplicationContextUtil;
import com.taobao.tair.TairManager;

public class TairStoreFactory {
	
	private static String tairManagerName = "tairManager" ;
	
	/**  Tair 存储管理器  tairManager 
	 * 	 其实不应该在这里被注入.
	 * 
	 *   需要建立 TairFactory 来注入 tairManager
	 *   供 Tair Store 来使用.
	 * 	 
	 * **/
	
	public static TairManager getTairManager(){
		return (TairManager)ApplicationContextUtil.getBean(tairManagerName);
	}
	
}
