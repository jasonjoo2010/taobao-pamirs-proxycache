package com.taobao.pamirs.cache.old;

import java.io.Serializable;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;

import com.taobao.pamirs.cache.framework.config.MethodConfig;
import com.taobao.pamirs.cache.store.StoreObject;
import com.taobao.pamirs.cache.util.CaCheProxyLog;
import com.taobao.tair.ResultCode;
import com.taobao.tair.TairManager;

/**
 * 兼容写老的缓存，稳定后删除
 * @author tiebi.hlw
 *
 */
public class OldCacheManager {
	
	
	private static final Log logger = CaCheProxyLog.LOGGER_DEFAULT;
	
	/**
	 * 是否兼容
	 */
	private boolean compatible;

	
	private TairManager oldTairManager;
	
	
	private String region;
	
	/**
	 * 只作商品兼容
	 */
	private int namespace;
	
	
	
	public void put(String beanName,MethodInvocation invocation, Object value) {
		
		try{
			String key=CacheUtils.getTairKey(region, invocation, beanName);
			
			StoreObject<String,Object> co = new StoreObject<String,Object>(value);		
			
			this.remove(key);
			ResultCode rc = oldTairManager.put(namespace, key,
					(Serializable) co, 0, 0);
			if (!rc.isSuccess()) {
				ResultCode rc1 = oldTairManager.put(namespace, key,
						(Serializable) co, 0, 0);
				if (!rc1.isSuccess())  {
					logger.error("Old Tair Cache failed to put object [namespace="
							+ namespace + ", key=" + key
							+ "]. Error Message : " + rc1.getMessage());
				}
			}
		}catch(Exception e){
			logger.error("OldCacheManager put error,"+beanName+"."+invocation.getMethod().getName()
					+" arg:"+invocation.getArguments()[0], e);
		}
	}
	
	
	
	public void remove(String beanName,MethodConfig method,Object[] params){
		try{
			String key=CacheUtils.getTairKey(region, beanName, method, params);
			this.remove(key);
		}catch(Exception e){
			logger.error("OldCacheManager put error,"+beanName+"."+method.getMethodName()
					+" arg:"+params[0], e);
		}
		
	}
	
	private void remove(String key) {
		ResultCode rc = oldTairManager.invalid(namespace, key);
		if (!rc.isSuccess()&&!ResultCode.DATANOTEXSITS.equals(rc.getCode())) {
			ResultCode rc1 = oldTairManager.invalid(namespace, key);
			if (!rc1.isSuccess()&&!ResultCode.DATANOTEXSITS.equals(rc1.getCode())) {
				logger.error("Old Tair Cache failed to invalid object [namespace="
						+ namespace + ", key=" + key
						+ "]. Error Message : " + rc1.getMessage());
			}
		}
	}

	public boolean isCompatible() {
		return compatible;
	}


	public void setCompatible(boolean compatible) {
		this.compatible = compatible;
	}


	public void setOldTairManager(TairManager oldTairManager) {
		this.oldTairManager = oldTairManager;
	}



	public void setRegion(String region) {
		this.region = region;
	}



	public int getNamespace() {
		return namespace;
	}



	public void setNamespace(int namespace) {
		this.namespace = namespace;
	}
	
	
	

}
