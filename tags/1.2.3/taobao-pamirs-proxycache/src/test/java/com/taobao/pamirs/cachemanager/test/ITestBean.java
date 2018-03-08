package com.taobao.pamirs.cachemanager.test;

import java.util.Map;

public interface ITestBean {
	public String getService(long serverId);
	public Map<String, Object> loadAllService();
	public String getSp(long spId) ;
	public String getProduct(long serverId);
	public String updateProduct(long productId);
	public void updateService(long serverId,String Str);
}
