package com.taobao.pamirs.cache.old;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;

import com.taobao.pamirs.cache.framework.config.MethodConfig;
import com.taobao.pamirs.cache.store.StoreObject;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;

/**
 * ��װ����˻��湤�߹����������
 * @author tiebi.hlw
 *
 */
public class CacheUtils {
	
	
	/**
	 * ��ȡcache proxy 1.0 ��tair����key��������ͬ��
	 * @param invocation
	 * @param beanName
	 * @return
	 */
	public  static String getTairKey(String region,MethodInvocation invocation,String beanName){
		
		if(invocation==null||beanName==null){
			return null;
		}
		
		Method m = invocation.getMethod();
		Object[] parameters = invocation.getArguments();
		
		if(parameters==null||parameters.length==0||parameters[0]==null){
			return null;
		}
		StringBuilder cacheKey = new StringBuilder();
		cacheKey.append(region)
				.append(generateCacheCode(beanName, m.getName(),m.getParameterTypes()))
				.append(parameters[0].toString());
		return cacheKey.toString();
	}
	
	
	
	
	public static String getTairKey(String region,String beanName,MethodConfig method,Object[] parameters){
		if(region==null||method==null||parameters==null||parameters.length<=0){
			return null;
		}
		
		StringBuilder cacheKey = new StringBuilder();
		cacheKey.append(region)
				.append(generateCacheCode(beanName, method.getMethodName(),method.getParameterTypes()))
				.append(parameters[0].toString());
		return cacheKey.toString();
		
	}
	
	
	
	
	
	/**
	 * ת�������ݶ���(com.taobao.pamirs.cache.store.StoreObject �����ֵ)
	 * @param result
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static Object getValue(Result<DataEntry> result) throws Exception{
		if(result==null||!result.isSuccess()){
			return null;
		}
		StoreObject<String,Object> storeObject = null;
		DataEntry data = result.getValue();
		if(data!=null&&data.getValue()!=null){
			storeObject = (StoreObject<String,Object>)data.getValue();
		}
		if(storeObject!=null){
			return storeObject.getObject();
		}
		return null;
	}
	
	
	private  static String generateCacheCode(String beanName, String methodName,
			Class<?>[] parameters) {
		String cacheCode = null;
		String parameter = "";

		for (int i = 0; i < parameters.length; i++) {
			Class<?> c = parameters[i];
			if (!parameter.equals("")) {
				parameter = parameter + ",";
			}

			if (c == String.class) {
				parameter = parameter + "String";
			} else if (c == int.class) {
				parameter = parameter + "int";
			} else if (c == Integer.class) {
				parameter = parameter + "Integer";
			} else if (c == long.class) {
				parameter = parameter + "long";
			} else if (c == Long.class) {
				parameter = parameter + "Long";
			} else if (c == short.class) {
				parameter = parameter + "short";
			} else if (c == Short.class) {
				parameter = parameter + "Short";
			} else {
				parameter = parameter + "Object";
			}
		}

		parameter = "{" + parameter + "}";

		cacheCode = beanName + "#" + methodName + "#" + parameter;
		return cacheCode;
	}
	
	private  static String generateCacheCode(String beanName, String methodName,
			List<Class<?>> parameters) {
		String cacheCode = null;
		String parameter = "";

		for (Class<?> c: parameters) {
			if (!parameter.equals("")) {
				parameter = parameter + ",";
			}

			if (c == String.class) {
				parameter = parameter + "String";
			} else if (c == int.class) {
				parameter = parameter + "int";
			} else if (c == Integer.class) {
				parameter = parameter + "Integer";
			} else if (c == long.class) {
				parameter = parameter + "long";
			} else if (c == Long.class) {
				parameter = parameter + "Long";
			} else if (c == short.class) {
				parameter = parameter + "short";
			} else if (c == Short.class) {
				parameter = parameter + "Short";
			} else {
				parameter = parameter + "Object";
			}
		}

		parameter = "{" + parameter + "}";

		cacheCode = beanName + "#" + methodName + "#" + parameter;
		return cacheCode;
	}

}
