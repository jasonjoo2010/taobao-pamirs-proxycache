package com.taobao.pamirs.cache.util.convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;

/**
 * MockCacheManager.
 * 
 * @author qiudao
 * @version 1.0
 * @since 2014年10月30日
 */
public class MockCacheManager implements InitializingBean {

	/**
	 * 通过 CacheConfig.xml 配置注入
	 **/
	private List<String> cacheConfig;
	private List<String> cacheCleanConfig;

	private Map<String, BeanCacheConfig> beanCacheConfigMap = new HashMap<String, BeanCacheConfig>();
	private Map<String, BeanCacheCleanConfig> beanCacheCleanConfigMap = new HashMap<String, BeanCacheCleanConfig>();

	private List<BeanCacheCleanConfig> beanCacheCleanConfigs = new ArrayList<BeanCacheCleanConfig>();
	private List<BeanCacheConfig> beanCacheConfigs = new ArrayList<BeanCacheConfig>();

	private Set<String> cacheBeanNameSet = new HashSet<String>();
	/**
	 * 通过 CacheConfig.xml 配置注入缓存配置默认值
	 */
	private String defaultCacheConfig;

	public void setDefaultCacheConfig(String defaultCacheConfig) {
		this.defaultCacheConfig = defaultCacheConfig;
	}

	// 通过缓存定义生成缓存 Map 结构
	public void setCacheConfig(List<String> aCcheConfig) {
		this.cacheConfig = aCcheConfig;
		for (String item : this.cacheConfig) {
			BeanCacheConfig config = new BeanCacheConfig(this.defaultCacheConfig, item);
			if (!beanCacheConfigMap.containsKey(config.getCacheCode())) {
				beanCacheConfigMap.put(config.getCacheCode(), config);
				cacheBeanNameSet.add(config.getBeanName());

				beanCacheConfigs.add(config);
			} else {
				throw new RuntimeException("CacheCode 重复" + config.getCacheCode());
			}
		}
	}

	// 通过缓存 clear 定义生成缓存 clear Map 结构
	public void setCacheCleanConfig(List<String> aCcheConfig) {
		this.cacheCleanConfig = aCcheConfig;
		for (String item : this.cacheCleanConfig) {
			BeanCacheCleanConfig config = new BeanCacheCleanConfig(item);
			if (!beanCacheCleanConfigMap.containsKey(config.getCacheCode())) {
				beanCacheCleanConfigMap.put(config.getCacheCode(), config);
				cacheBeanNameSet.add(config.getBeanName());

				beanCacheCleanConfigs.add(config);
			} else {
				throw new RuntimeException("Cache clean CacheCode 重复" + config.getCacheCode());
			}
		}
	}

	// 缓存是否初始化标志位.
	private boolean cacheInitFlag;

	public void setCacheInitFlag(boolean cacheInitFlag) {
		this.cacheInitFlag = cacheInitFlag;
	}

	// 缓存是否初始化标志位.
	private boolean cacheUseFlag;

	public void setCacheUseFlag(boolean cacheUseFlag) {
		this.cacheUseFlag = cacheUseFlag;
	}

	public boolean isUseCache() {
		return this.cacheUseFlag;
	}

	public List<String> getCacheConfig() {
		return cacheConfig;
	}

	public List<String> getCacheCleanConfig() {
		return cacheCleanConfig;
	}

	public String getDefaultCacheConfig() {
		return defaultCacheConfig;
	}

	public boolean isCacheInitFlag() {
		return cacheInitFlag;
	}

	public boolean isCacheUseFlag() {
		return cacheUseFlag;
	}

	public List<BeanCacheCleanConfig> getBeanCacheCleanConfigs() {
		return beanCacheCleanConfigs;
	}

	public void setBeanCacheCleanConfigs(List<BeanCacheCleanConfig> beanCacheCleanConfigs) {
		this.beanCacheCleanConfigs = beanCacheCleanConfigs;
	}

	public List<BeanCacheConfig> getBeanCacheConfigs() {
		return beanCacheConfigs;
	}

	public void setBeanCacheConfigs(List<BeanCacheConfig> beanCacheConfigs) {
		this.beanCacheConfigs = beanCacheConfigs;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub

	}

	public class BeanCacheCleanConfig {
		private String beanName;
		private String methodName;
		private String parameterTypes;
		private String cacheCode;
		private String[] cacheCleanCodes;

		public String[] split(String str, char splitChar, char checkChar) {
			List<String> result = new ArrayList<String>();
			String[] list = str.split("\\" + splitChar);
			for (int i = 0; i < list.length; i++) {
				if (list[i].indexOf(checkChar) >= 0) {
					result.add(list[i]);
				} else {
					result.set(result.size() - 1, result.get(result.size() - 1) + splitChar + list[i]);
				}
			}
			return (String[]) result.toArray(new String[0]);
		}

		public BeanCacheCleanConfig(String str) {
			String[] properties = split(str, ',', '=');
			for (String s : properties) {
				String[] nameAndValue = s.split("=");
				nameAndValue[0] = nameAndValue[0].trim();
				nameAndValue[1] = nameAndValue[1].trim();
				if (nameAndValue[0].equals("cacheCleanCodes")) {
					this.cacheCleanCodes = nameAndValue[1].split(";");
				} else if (nameAndValue[0].equals("beanName")) {
					this.beanName = nameAndValue[1];
				} else if (nameAndValue[0].equals("methodName")) {
					this.methodName = nameAndValue[1];
				} else if (nameAndValue[0].equals("parameterTypes")) {
					this.parameterTypes = nameAndValue[1];
				} else {
					throw new RuntimeException("不支持的属性：" + nameAndValue[0]);
				}
			}

			/*
			 * 对于4个缓存配置的关键属性.不允许使用默认值. *
			 */
			if (this.beanName == null || this.methodName == null || this.parameterTypes == null
					|| this.cacheCleanCodes == null) {

				throw new RuntimeException("缓存清空配置信息不足.初始化失败 :" + beanName + ";" + methodName + ";" + parameterTypes
						+ ";");

			}

			// 检查 参数列表是否合理
			if (this.parameterTypes != null) {
				int start = this.parameterTypes.indexOf("{");
				int end = this.parameterTypes.indexOf("}");

				String subParameterTypes = this.parameterTypes.substring(start + 1, end);
				String[] parameterTypesArray = subParameterTypes.split(",");

				for (int i = 0; i < parameterTypesArray.length; i++) {
					if (!parameterTypesArray[i].equals("long") && !parameterTypesArray[i].equals("Long")
							&& !parameterTypesArray[i].equals("String") && !parameterTypesArray[i].equals("int")
							&& !parameterTypesArray[i].equals("Integer") && !parameterTypesArray[i].equals("short")
							&& !parameterTypesArray[i].equals("Short") && !parameterTypesArray[i].equals("Object")) {

						throw new RuntimeException("缓存参数列表配置信息错误,只能使用  String,long,int,short:" + cacheCode + ";"
								+ parameterTypes + ";");
					}
				}
			}

			this.cacheCode = BeanCacheConfig.generateCacheCode(this.beanName, this.methodName, this.parameterTypes);

		}

		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("beanName=" + beanName);
			builder.append(",methodName=" + methodName);
			builder.append(",cacheCleanCodes=");
			for (int i = 0; i < this.cacheCleanCodes.length; i++) {
				if (i > 0) {
					builder.append("#");
				}
				builder.append(this.cacheCleanCodes[i]);
			}
			return builder.toString();
		}

		public String getBeanName() {
			return beanName;
		}

		public String getMethodName() {
			return methodName;
		}

		public String[] getCacheCleanCodes() {
			return cacheCleanCodes;
		}

		public String getCacheCode() {
			return cacheCode;
		}

		public String getParameterTypes() {
			return parameterTypes;
		}

		public void setParameterTypes(String parameterTypes) {
			this.parameterTypes = parameterTypes;
		}

		public void setBeanName(String beanName) {
			this.beanName = beanName;
		}

		public void setMethodName(String methodName) {
			this.methodName = methodName;
		}

		public void setCacheCode(String cacheCode) {
			this.cacheCode = cacheCode;
		}

		public void setCacheCleanCodes(String[] cacheCleanCodes) {
			this.cacheCleanCodes = cacheCleanCodes;
		}
	}

	public static class BeanCacheConfig {

		private String beanName;
		private String methodName;
		private String parameterTypes;
		private String cacheName;

		private String storeType;
		private String storeTairRegion;
		private int storeTairNameSpace;
		private String returnType = "serializable";
		private String groupName;
		private long expireTimes;
		private String cleanTime;

		private String cacheCode;

		public static String generateCacheCode(String beanName, String methodName, Class<?>[] parameters) {
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

		public static String generateCacheCode(String beanName, String methodName, String parameters) {

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
					result.set(result.size() - 1, result.get(result.size() - 1) + splitChar + list[i]);
				}
			}
			return (String[]) result.toArray(new String[0]);
		}

		public BeanCacheConfig(String defaultItem, String item) {
			// 默认数值设置.
			if (defaultItem != null && !"".equals(defaultItem.trim())) {
				initDefaultCacheConfig(defaultItem);
			}

			/**
			 * private String beanName; private String methodName; private String parameterTypes; private String
			 * cacheName; private String storeType; private String storeTairRegion; private String storeTairNameSpace;
			 * private String returnType = "serializable"; private String groupName; private long expireTimes; private
			 * String cleanTime;
			 **/
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
					throw new RuntimeException("不支持的属性：" + nameAndValue[0]);
				}
			}

			/*
			 * 对于5个缓存配置的关键属性.不允许使用默认值. *
			 */
			if (this.cacheName == null || this.beanName == null || this.methodName == null
					|| this.parameterTypes == null) {

				throw new RuntimeException("缓存配置信息不足.初始化失败 :" + cacheName + ";" + beanName + ";" + methodName + ";"
						+ parameterTypes + ";");

			}

			// 检查参数列表是否合理
			if (this.parameterTypes != null) {
				int start = this.parameterTypes.indexOf("{");
				int end = this.parameterTypes.indexOf("}");

				String subParameterTypes = this.parameterTypes.substring(start + 1, end);
				String[] parameterTypesArray = subParameterTypes.split(",");

				for (int i = 0; i < parameterTypesArray.length; i++) {
					if (!parameterTypesArray[i].equals("long") && !parameterTypesArray[i].equals("Long")
							&& !parameterTypesArray[i].equals("String") && !parameterTypesArray[i].equals("int")
							&& !parameterTypesArray[i].equals("Integer") && !parameterTypesArray[i].equals("short")
							&& !parameterTypesArray[i].equals("Short") && !parameterTypesArray[i].equals("Object")) {

						throw new RuntimeException("缓存参数列表配置信息错误,只能使用  String,long,int,short:" + cacheName + ";"
								+ parameterTypes + ";");
					}
				}
			}
			this.cacheCode = generateCacheCode(this.beanName, this.methodName, this.parameterTypes);
		}

		/**
		 * 初始化默认配置--add by yuanhong<br>
		 * 暂时支持默认值的属性有：<br>
		 * returnType、storeType、expireTimes、loadDataMethodName、<br>
		 * isInitialData、cleanTime、groupName<br>
		 */
		private void initDefaultCacheConfig(String defaultItem) {

			/**
			 * private String storeType; private String storeTairRegion; private String storeTairNameSpace; private
			 * String returnType; private String groupName; private long expireTimes; private String cleanTime;
			 **/

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
					throw new RuntimeException("加载缓存默认值出错，不支持的属性：" + nameAndValue[0]);
				}
			}
		}

		public String toString() {
			String result = "cacheName=" + this.cacheName + ",beanName=" + this.beanName + ",methodName="
					+ this.methodName;
			return result;
		}

		public static String getDEFAULT_GROUP_NAME() {
			return "";
		}

		public static long getDEFAULT_EXPIRE_TIMES() {
			return 0l;
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
}
