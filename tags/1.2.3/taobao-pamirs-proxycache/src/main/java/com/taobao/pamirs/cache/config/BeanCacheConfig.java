package com.taobao.pamirs.cache.config;

import java.util.ArrayList;
import java.util.List;

import com.taobao.pamirs.cache.store.Store;

/**
 * �������ö���
 * 
 * @author xuannan
 * 
 */
public class BeanCacheConfig {
	
	public static String DEFAULT_STORE_TYPE;
	public static String DEFAULT_STORE_TAIR_REGION;
	public static String DEFAULT_STORE_TAIR_NAME_SPACE;
	public static String DEFAULT_RETURN_TYPE;
	public static String DEFAULT_GROUP_NAME;
	public static long   DEFAULT_EXPIRE_TIMES;
	public static String DEFAULT_CLEAN_TIME;
	
	public static String DEFAULT_TAIR_RETURN_TYPE = "serializable";

	private String beanName;
	private String methodName;
	private String parameterTypes;
	private String cacheName;

	private String storeType;
	private String storeTairRegion;
	private int storeTairNameSpace;
	private String returnType = "serializable";
	private String groupName;
	private long   expireTimes;
	private String cleanTime;

	private String cacheCode;
	
	public static String generateCacheCode(String beanName, String methodName,
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

	public static String generateCacheCode(String beanName, String methodName,
			String parameters) {

		String cacheCode = null;
		cacheCode = beanName + "#" + methodName + "#" + parameters;

		return cacheCode;
	}

	public String[] split(String str, char splitChar, char checkChar) {
		List<String> result = new ArrayList<String>();
		String[] list = str.split("\\" + splitChar);
		for (int i = 0; i < list.length; i++) {
			if (list[i].indexOf(checkChar) >= 0) {
				result.add(list[i]);
			} else {
				result.set(result.size() - 1, result.get(result.size() - 1)
						+ splitChar + list[i]);
			}
		}
		return (String[]) result.toArray(new String[0]);
	}

	public BeanCacheConfig(String defaultItem, String item) {
		//Ĭ����ֵ����.
		if (defaultItem != null && !"".equals(defaultItem.trim())){
			initDefaultCacheConfig(defaultItem);
		}
		
		/**
		 	private String beanName;
			private String methodName;
			private String parameterTypes;
			private String cacheName;
		
			private String storeType;
			private String storeTairRegion;
			private String storeTairNameSpace;
			private String returnType = "serializable";
			private String groupName;
			private long   expireTimes;
		    private String cleanTime;
		    
		 * **/
		String[] properties = split(item, ',', '=');
		for (String s : properties) {
			String[] nameAndValue = s.split("=");
			nameAndValue[0] = nameAndValue[0].trim();
			nameAndValue[1] = nameAndValue[1].trim();
			if (nameAndValue[0].equals("beanName")) {
				this.beanName = nameAndValue[1];
			} else if (nameAndValue[0].equals("methodName")) {
				this.methodName = nameAndValue[1];
			} else if (nameAndValue[0].equals("parameterTypes")) {
				this.parameterTypes = nameAndValue[1];
			} else if (nameAndValue[0].equals("cacheName")) {
				this.cacheName = nameAndValue[1];
			} else if (nameAndValue[0].equals("storeType")) {
				this.storeType = nameAndValue[1];
			} else if (nameAndValue[0].equals("storeTairRegion")) {
				this.storeTairRegion = nameAndValue[1];
			} else if (nameAndValue[0].equals("storeTairNameSpace")) {
				this.storeTairNameSpace =Integer.parseInt(nameAndValue[1]);
			} else if (nameAndValue[0].equals("returnType")) {
				this.returnType = nameAndValue[1];
			} else if (nameAndValue[0].equals("groupName")) {
				this.groupName = nameAndValue[1];
			} else if (nameAndValue[0].equals("expireTimes")) {
				this.expireTimes = Long.parseLong(nameAndValue[1]);				
			} else if (nameAndValue[0].equals("cleanTime")) {
				this.cleanTime = nameAndValue[1];
			} else {
				throw new RuntimeException("��֧�ֵ����ԣ�" + nameAndValue[0]);
			}
		}

		/*
		 * ����5���������õĹؼ�����.������ʹ��Ĭ��ֵ.
		 * 
		 * *
		 */
		if (this.cacheName == null 
			|| this.beanName == null 
			|| this.methodName == null
			|| this.parameterTypes == null) {

			throw new RuntimeException("����������Ϣ����.��ʼ��ʧ�� :" 
					+ cacheName + ";"
				    + beanName + ";" 
				    + methodName + ";"
					+ parameterTypes + ";");

		}

		// �������б��Ƿ����
		if (this.parameterTypes != null) {
			int start = this.parameterTypes.indexOf("{");
			int end = this.parameterTypes.indexOf("}");

			String subParameterTypes = this.parameterTypes.substring(start + 1,
					end);
			String[] parameterTypesArray = subParameterTypes.split(",");

			for (int i = 0; i < parameterTypesArray.length; i++) {
				if (!parameterTypesArray[i].equals("long")
						&& !parameterTypesArray[i].equals("Long")
						&& !parameterTypesArray[i].equals("String")
						&& !parameterTypesArray[i].equals("int")
						&& !parameterTypesArray[i].equals("Integer")
						&& !parameterTypesArray[i].equals("short")
						&& !parameterTypesArray[i].equals("Short")
						&& !parameterTypesArray[i].equals("Object")) {

					throw new RuntimeException(
							"��������б�������Ϣ����,ֻ��ʹ��  String,long,int,short:"
									+ cacheName + ";" + parameterTypes + ";");
				}
			}
		}

		// �������� Tair ģʽ.����ֵ�Ƿ����л�.
		if (this.storeType.equals(Store.STORE_TYPE_TAIR)
				&& !this.returnType.equals(DEFAULT_TAIR_RETURN_TYPE)) {
			throw new RuntimeException("���� Tair �洢 ,��������ֵ���������л�����" + cacheName
					+ ";" + parameterTypes + ";" + returnType + ";");

		}

		this.cacheCode = generateCacheCode(this.beanName, this.methodName,
				this.parameterTypes);
	}

	/**
	 * ��ʼ��Ĭ������--add by yuanhong<br>
	 * ��ʱ֧��Ĭ��ֵ�������У�<br>
	 * returnType��storeType��expireTimes��loadDataMethodName��<br>
	 * isInitialData��cleanTime��groupName<br>
	 */
	private void initDefaultCacheConfig(String defaultItem) {
		
		/**
		 	private String storeType;
			private String storeTairRegion;
			private String storeTairNameSpace;
			private String returnType;
			private String groupName;
			private long   expireTimes;
			private String cleanTime;
		 * **/
		
		String[] properties = split(defaultItem, ',', '=');
		for (String s : properties) {
			String[] nameAndValue = s.split("=");
			nameAndValue[0] = nameAndValue[0].trim();
			nameAndValue[1] = nameAndValue[1].trim();
			if (nameAndValue[0].equals("storeType")) {
				this.storeType = nameAndValue[1];
			} else if (nameAndValue[0].equals("storeTairRegion")) {
				this.storeTairRegion = nameAndValue[1];
			} else if (nameAndValue[0].equals("storeTairNameSpace")) {
				this.storeTairNameSpace = Integer.parseInt(nameAndValue[1]);				
			} else if (nameAndValue[0].equals("returnType")) {
				this.returnType = nameAndValue[1];
			} else if (nameAndValue[0].equals("groupName")) {
				this.groupName = nameAndValue[1];
			} else if (nameAndValue[0].equals("expireTimes")) {
				this.expireTimes = Long.parseLong(nameAndValue[1]);
			} else if (nameAndValue[0].equals("cleanTime")) {
				this.cleanTime = nameAndValue[1];				
			} else {
				throw new RuntimeException("���ػ���Ĭ��ֵ������֧�ֵ����ԣ�" + nameAndValue[0]);
			}
		}
	}

	public String toString() {
		String result = "cacheName=" 
			+ this.cacheName 
			+ ",beanName="
			+ this.beanName 
			+ ",methodName=" 
			+ this.methodName;
		return result;
	}

	public static String getDEFAULT_GROUP_NAME() {
		return DEFAULT_GROUP_NAME;
	}

	public static long getDEFAULT_EXPIRE_TIMES() {
		return DEFAULT_EXPIRE_TIMES;
	}

	public String getBeanName() {
		return beanName;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getParameterTypes() {
		return parameterTypes;
	}

	public String getCacheName() {
		return cacheName;
	}

	public String getStoreType() {
		return storeType;
	}

	public String getStoreTairRegion() {
		return storeTairRegion;
	}

	public int getStoreTairNameSpace() {
		return storeTairNameSpace;
	}

	public String getReturnType() {
		return returnType;
	}

	public String getGroupName() {
		return groupName;
	}

	public long getExpireTimes() {
		return expireTimes;
	}

	public String getCacheCode() {
		return cacheCode;
	}

	public String getCleanTime() {
		return cleanTime;
	}
	
}
