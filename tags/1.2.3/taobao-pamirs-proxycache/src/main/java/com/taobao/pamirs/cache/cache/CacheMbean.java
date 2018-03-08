package com.taobao.pamirs.cache.cache;

import java.lang.reflect.Method;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.pamirs.cache.AbstractDynamicMBean;
import com.taobao.pamirs.cache.ApplicationContextUtil;
import com.taobao.pamirs.cache.aop.advice.CacheManagerRoundAdvice;
import com.taobao.pamirs.cache.config.BeanCacheConfig;

public class CacheMbean<K,V> extends AbstractDynamicMBean {
	private static  Log log = LogFactory.getLog(CacheMbean.class);
	
	Cache<K,V> cache = null;
	
	public CacheMbean(Cache<K,V> cache){
		this.cache = cache;
	}
	
	public String getCacheName(){
		return this.cache.getCacheName();
	}
	public String getStoreType(){
		return this.cache.getStoreType();
	}
	public String getStoreCount(){
		return this.cache.getDataCount();
	}	
	public long getMemoryHits(){
		return this.cache.getSuccesCount();
	}	
	public long getDiskHits(){
		return this.cache.getFailCount();
	}
	public String getHitRate(){
		return this.cache.getHitRate();
	}	
	public boolean getIsUseCache(){
		return this.cache.getIsUseCache();
	}	
	public long getExpireTime(){
		return this.cache.getExpireTimes();
	}	
	public long getReadTime(){
		return this.cache.getAvagReadTime();
	}
	public long getWriteTime(){
		return this.cache.getAvagWriteTime();
	}	
	public long getRemoveCount(){
		return this.cache.getRemoveCount();
	}
	public String getCleanTime(){
		return this.cache.getCleanTime();
	}
	public String remove(K key){
		try {
			this.cache.remove(key);
		} catch (Exception e) {
			return "Cache Remove Failure Key:" + key;
		}
		return "Cache Remove Successfully Key:" + key;
		
	}
	public V get(K key){
		return this.cache.get(key);
	}
	
	/**
	   这个方法通过 Cache 获取真实值.
	 * **/
	
	@SuppressWarnings("unchecked")
	public V getRealValue(K key){
		
		V result = null;
		
		try {
			BeanCacheConfig cacheConfig = this.cache.getBeanCacheConfig();		
			Object bean = ApplicationContextUtil.getBean(cacheConfig.getBeanName());
			
			String paramterType = cacheConfig.getParameterTypes();
			String[] keyItems = key.toString().split(CacheManagerRoundAdvice.VALUE_KEY_SPLITE_SIGN);
			
			int start = paramterType.indexOf("{");
			int end = paramterType.indexOf("}");

			String subParameterTypes = paramterType.substring(start + 1,end);
			String[] parameterTypesArray = subParameterTypes.split(",");
			if(parameterTypesArray.length != keyItems.length){
				result = (V)("jmx的参数数量和接口的参数数量不一致,请求："+key.toString() + "接口参数:"+paramterType);
				return result;
			}
			Class<?>[] methodParameter = new Class[parameterTypesArray.length];
			
			
			Object [] methodArgs = new Object[parameterTypesArray.length];
			for (int i = 0; i < parameterTypesArray.length; i++) {
				if (parameterTypesArray[i].equals("long")){
					methodParameter[i] = long.class;	
					methodArgs[i] = new Long(keyItems[i]).longValue();
				}else if(parameterTypesArray[i].equals("Long")){
				    methodParameter[i] = Long.class;	
				    methodArgs[i] = new Long(keyItems[i]);
				}else if(parameterTypesArray[i].equals("int")){
				    methodParameter[i] = int.class;	
				    methodArgs[i] = new Integer(keyItems[i]).intValue();
				}else if(parameterTypesArray[i].equals("Integer")){
				    methodParameter[i] = Integer.class;		
				    methodArgs[i] = new Integer(keyItems[i]);
				}else if(parameterTypesArray[i].equals("short")){
				    methodParameter[i] = short.class;	
				    methodArgs[i] = new Short(keyItems[i]).shortValue();
				}else if(parameterTypesArray[i].equals("Short")){
				    methodParameter[i] = Short.class;	 
				    methodArgs[i] = new Short(keyItems[i]);
				}else if(parameterTypesArray[i].equals("String")){
				    methodParameter[i] = String.class;
				    methodArgs[i] = new String(keyItems[i]);
				}			
			}
		
			Class<?> beanClass = bean.getClass();	
			Method beanMethod = beanClass.getMethod(cacheConfig.getMethodName(), methodParameter);
						
			result = (V)beanMethod.invoke(bean, methodArgs);
			
		} catch (Exception e) {
			log.error("getRealValue Error :" + e.getMessage(),e);
		}
				
		return result;
	}
	public String put(K key ,V value){
		try {
			this.cache.put(key, value);
		} catch (Exception e) {
			return "Cache Put Failure Key:" + key +" Value:"+ value;
		}
		return "Cache Put Successfully Key:" + key +" Value:"+ value;
	}
	
	protected void buildDynamicMBeanInfo() {
		MBeanAttributeInfo[] dAttributes = new MBeanAttributeInfo[] { 
				new MBeanAttributeInfo("cacheName", "String", "缓存名称", true, false, false),
				new MBeanAttributeInfo("storeType", "String", "缓存类型", true, false, false),
				new MBeanAttributeInfo("storeCount", "String", "缓存数据量", true, false, false),
				new MBeanAttributeInfo("memoryHits", "long", "命中次数", true, false, false),
				new MBeanAttributeInfo("diskHits", "long", "未命中次数", true, false, false),
				new MBeanAttributeInfo("hitRate", "double", "缓存命中率", true, false, false),				
				new MBeanAttributeInfo("isUseCache", "boolean", "是否使用缓存", true, false, false),
				new MBeanAttributeInfo("expireTime", "long", "缓存数据失效时间", true, false, false),
				new MBeanAttributeInfo("readTime", "long", "缓存读取时间", true, false, false),
				new MBeanAttributeInfo("writeTime", "long", "缓存存入时间", true, false, false),
				new MBeanAttributeInfo("removeCount", "long", "缓存删除次数", true, false, false),
				new MBeanAttributeInfo("cleanTime", "String", "缓存清理时间", true, false, false)				
				};

		MBeanOperationInfo[] dOperations = new MBeanOperationInfo[] { 
				new MBeanOperationInfo("remove", "缓存删除",new MBeanParameterInfo[] { new MBeanParameterInfo(
						"CacheRemove", "java.lang.String","输入String Key 进行缓存的 remove 操作.")}, "String",MBeanOperationInfo.ACTION),
				new MBeanOperationInfo("put", "缓存数据写入",new MBeanParameterInfo[] { 
						new MBeanParameterInfo("CachePut Key", "java.lang.String","输入String Key.多参数用@@分隔."),
						new MBeanParameterInfo("CachePut Value", "java.lang.String","输入String Value.")
						}, "String",MBeanOperationInfo.ACTION),
				new MBeanOperationInfo("get", "缓存数据读取",new MBeanParameterInfo[] { new MBeanParameterInfo(
						"CacheGet", "java.lang.String","输入String Key 进行缓存的 get 操作.多参数用@@分隔.")}, "String",MBeanOperationInfo.ACTION),		
			    new MBeanOperationInfo("getRealValue", "运行期数据读取",new MBeanParameterInfo[] { new MBeanParameterInfo(
					    "DiskGet", "java.lang.String","输入String Key 进行Disk的 get 操作.多参数用@@分隔.")}, "String",MBeanOperationInfo.ACTION)						
				};
		dMBeanInfo = new MBeanInfo(this.getClass().getName(), "HJ-Cache",
				dAttributes, null, dOperations, null);
	}
}
