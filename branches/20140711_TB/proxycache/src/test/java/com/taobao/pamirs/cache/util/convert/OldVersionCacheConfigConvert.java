package com.taobao.pamirs.cache.util.convert;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.taobao.pamirs.cache.framework.config.CacheBean;
import com.taobao.pamirs.cache.framework.config.CacheCleanBean;
import com.taobao.pamirs.cache.framework.config.CacheCleanMethod;
import com.taobao.pamirs.cache.framework.config.CacheModule;
import com.taobao.pamirs.cache.framework.config.MethodConfig;
import com.taobao.pamirs.cache.util.ConfigUtil;
import com.taobao.pamirs.cache.util.convert.MockCacheManager.BeanCacheCleanConfig;
import com.taobao.pamirs.cache.util.convert.MockCacheManager.BeanCacheConfig;

/**
 * 讲老版本的cache配置文件转换成新版本的配置文件.
 * 
 * @author qiudao
 * @version 1.0
 * @since 2014年10月30日
 */
public class OldVersionCacheConfigConvert {

	public static void convertToNewFile(String path) throws Exception {
		File file = FileHandler.covertFile(path);
		ApplicationContext applicationContext = new FileSystemXmlApplicationContext("file:" + file.getAbsolutePath());
		MockCacheManager cacheManager = (MockCacheManager) applicationContext.getBean("cacheManager");
		System.out.println("CacheConfig Total:" + cacheManager.getCacheConfig().size());
		System.out.println("CacheCleanConfig Total:" + cacheManager.getCacheCleanConfig().size());

		CacheModule cm = covert(cacheManager);
		int cacheMethodCount = 0;
		int cacheCleanMethodCount = 0;
		for (CacheBean tmp : cm.getCacheBeans()) {
			cacheMethodCount += tmp.getCacheMethods().size();
		}
		for (CacheCleanBean tmp : cm.getCacheCleanBeans()) {
			cacheCleanMethodCount += tmp.getMethods().size();
		}
		System.out.println("Convert CacheConfig Total:" + cacheMethodCount);
		System.out.println("Convert CacheCleanConfig Total:" + cacheCleanMethodCount);
		String xml = "<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n";
		xml += toXml(cm);
		File f2 = new File(path.substring(0, path.indexOf(".xml")) + "-new.xml");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f2)));
		bw.write(xml);
		file.delete();
		bw.flush();
		bw.close();
	}

	public static String toXml(CacheModule cacheModule) {
		return ConfigUtil.convertCacheConfigModuleToString(cacheModule);
	}

	public static CacheModule covert(MockCacheManager cacheManager) {
		CacheModule cacheModule = new CacheModule();
		List<CacheBean> cacheBeans = new ArrayList<CacheBean>();
		cacheModule.setCacheBeans(cacheBeans);
		List<CacheCleanBean> cacheCleanBeans = new ArrayList<CacheCleanBean>();
		cacheModule.setCacheCleanBeans(cacheCleanBeans);

		List<BeanCacheCleanConfig> cacheCleanConfigs = cacheManager.getBeanCacheCleanConfigs();
		Map<String, CacheCleanBean> cacheCleanBeanMap = new HashMap<String, CacheCleanBean>();
		for (BeanCacheCleanConfig tmp : cacheCleanConfigs) {
			CacheCleanBean cb = cacheCleanBeanMap.get(tmp.getBeanName());
			if (cb == null) {
				cb = new CacheCleanBean();
				cb.setBeanName(tmp.getBeanName());
				cacheCleanBeanMap.put(tmp.getBeanName(), cb);
			}

			CacheCleanMethod mc = new CacheCleanMethod();
			mc.setMethodName(tmp.getMethodName());
			mc.setParameterTypes(getParameterTypes(tmp.getParameterTypes()));
			mc.setCleanMethods(getCleanMethods(tmp.getBeanName(), tmp.getCacheCleanCodes()));

			if (cb.getMethods() == null) {
				cb.setMethods(new ArrayList<CacheCleanMethod>());
			}
			cb.getMethods().add(mc);
		}
		for (String key : cacheCleanBeanMap.keySet()) {
			cacheCleanBeans.add(cacheCleanBeanMap.get(key));
		}

		Map<String, CacheBean> cacheBeanMap = new HashMap<String, CacheBean>();
		List<BeanCacheConfig> cacheConfigs = cacheManager.getBeanCacheConfigs();
		for (BeanCacheConfig tmp : cacheConfigs) {
			CacheBean cb = cacheBeanMap.get(tmp.getBeanName());
			if (cb == null) {
				cb = new CacheBean();
				cb.setBeanName(tmp.getBeanName());
				cacheBeanMap.put(tmp.getBeanName(), cb);
			}

			MethodConfig mc = new MethodConfig();
			mc.setMethodName(tmp.getMethodName());
			mc.setParameterTypes(getParameterTypes(tmp.getParameterTypes()));

			if (cb.getCacheMethods() == null) {
				cb.setCacheMethods(new ArrayList<MethodConfig>());
			}
			cb.getCacheMethods().add(mc);
		}
		for (String key : cacheBeanMap.keySet()) {
			cacheBeans.add(cacheBeanMap.get(key));
		}

		return cacheModule;
	}

	private static List<MethodConfig> getCleanMethods(String beanName, String[] cacheCleanCodes) {
		List<MethodConfig> mcs = new ArrayList<MethodConfig>();
		if (cacheCleanCodes == null)
			return mcs;
		for (String tmp : cacheCleanCodes) {
			String[] strs = tmp.split("#");
			if (strs == null)
				continue;
			if (strs.length != 3) {
				System.out.println("Error cacheCleanBean config:" + tmp);
				continue;
			}

			if (!beanName.equals(strs[0])) {
				System.out.println("Not support cacheCleanBean config:" + tmp + ", bean name not equal!!!!!!" + tmp
						+ ",beanName:" + beanName);
				continue;
			}

			MethodConfig mc = new MethodConfig();
			mcs.add(mc);

			mc.setMethodName(strs[1]);
			mc.setParameterTypes(getParameterTypes(strs[2]));
		}
		return mcs;
	}

	private static List<Class<?>> getParameterTypes(String parameterTypes) {
		List<Class<?>> pts = new ArrayList<Class<?>>();
		if (parameterTypes != null) {
			int start = parameterTypes.indexOf("{");
			int end = parameterTypes.indexOf("}");

			String subParameterTypes = parameterTypes.substring(start + 1, end);
			String[] parameterTypesArray = subParameterTypes.split(",");
			if (parameterTypesArray == null || parameterTypesArray.length == 0)
				return pts;

			for (int i = 0; i < parameterTypesArray.length; i++) {
				if (parameterTypesArray[i].equals("long")) {
					pts.add(long.class);
				} else if (parameterTypesArray[i].equals("int")) {
					pts.add(int.class);
				} else if (parameterTypesArray[i].equals("short")) {
					pts.add(short.class);
				} else if (parameterTypesArray[i].equals("Short")) {
					pts.add(Short.class);
				} else if (parameterTypesArray[i].equals("Integer")) {
					pts.add(Integer.class);
				} else if (parameterTypesArray[i].equals("String")) {
					pts.add(String.class);
				} else if (parameterTypesArray[i].equals("Long")) {
					pts.add(Long.class);
				} else if (parameterTypesArray[i].equals("Object")) {
					pts.add(Object.class);
				}
			}
		}

		return pts;
	}
}
