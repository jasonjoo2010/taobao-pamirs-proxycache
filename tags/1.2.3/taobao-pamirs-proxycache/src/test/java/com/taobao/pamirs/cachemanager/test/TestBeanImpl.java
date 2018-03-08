package com.taobao.pamirs.cachemanager.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestBeanImpl implements ITestBean {
	
	public String getService(long serverId) {
		 return "service-" + serverId;
	}
	
	public String updateProduct(long productId) {
		 return "product" +  "-" + productId;
	}
	public String getProduct(long productId) {
		 return "product" +  "-" + productId;
	}
	public String getSp(long spId) {
		 return "shangping" +  "-" + spId;
	}
	public void updateService(long serverId,String Str){
		System.out.println("update " + serverId + "  = " + Str);
	}
	
	public void deleteService(){
		System.out.println("clear Service");
	}
	
	public Map<String,Object> loadAllService(){
		Map<String,Object> result = new ConcurrentHashMap<String,Object>();
		for(int i=0;i<10;i++){
			result.put(i+"", "xuannan-" + i);
		}
		return result;
	}
}
