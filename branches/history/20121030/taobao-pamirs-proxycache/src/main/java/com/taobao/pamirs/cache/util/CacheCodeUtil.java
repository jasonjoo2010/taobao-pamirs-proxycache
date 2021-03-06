package com.taobao.pamirs.cache.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.taobao.pamirs.cache.framework.config.MethodConfig;

/**
 * 缓存Code辅助类
 * 
 * @author xiaocheng 2012-11-2
 */
public class CacheCodeUtil {

	/**
	 * Key的主分隔符<br>
	 * 格式：regionbeanName#methodName#{String}
	 */
	public static final String KEY_SPLITE_SIGN = "#";
	/**
	 * key中方法参数的分隔符<br>
	 * 格式：{String|Long}
	 */
	public static final String KEY_PARAMS_SPLITE_SIGN = "|";

	/** region分隔符 */
	public static final String REGION_SPLITE_SIGN = "@";

	/**
	 * 取得最终的缓存Code中参数值分隔符<br>
	 * 格式：regionbeanName#methodName#{String,Long}abc@@123
	 */
	public static final String CODE_PARAM_VALUES_SPLITE_SIGN = "@@";

	/**
	 * 取得最终的缓存Code<br>
	 * 格式：regionbeanName#methodName#{String|Long}abc@@123
	 * 
	 * @param region
	 * @param beanName
	 * @param methodConfig
	 * @param parameters
	 * @return
	 */
	public static String getCacheCode(String region, String beanName,
			MethodConfig methodConfig, Object[] parameters) {
		// 最终的缓存code
		StringBuilder code = new StringBuilder();

		// 1. region
		// 2. bean + method + parameter
		code.append(getCacheAdapterKey(region, beanName, methodConfig));

		// 3. value
		if (parameters != null) {
			StringBuilder valus = new StringBuilder();
			for (Object param : parameters) {
				if (valus.length() != 0) {
					valus.append(CODE_PARAM_VALUES_SPLITE_SIGN);
				}

				valus.append(param == null ? "null" : param.toString());
			}
			code.append(valus.toString());
		}

		return code.toString();
	}

	/**
	 * 缓存适配器的key<br>
	 * 格式：regionbeanName#methodName#{String|Long}
	 * 
	 * @param region
	 * @param beanName
	 * @param methodConfig
	 * @return
	 */
	public static String getCacheAdapterKey(String region, String beanName,
			MethodConfig methodConfig) {
		Assert.notNull(methodConfig);

		// 最终的key
		StringBuilder key = new StringBuilder();

		// 1. region
		if (StringUtils.isNotBlank(region))
			key.append(region).append(REGION_SPLITE_SIGN);

		// 2. bean + method + parameter
		String methodName = methodConfig.getMethodName();
		List<Class<?>> parameterTypes = methodConfig.getParameterTypes();

		key.append(beanName).append(KEY_SPLITE_SIGN);
		key.append(methodName).append(KEY_SPLITE_SIGN);
		key.append(parameterTypesToString(parameterTypes));

		return key.toString();

	}

	/**
	 * 参数toString，格式{String|int}
	 * 
	 * @param parameterTypes
	 * @return
	 */
	public static String parameterTypesToString(List<Class<?>> parameterTypes) {
		StringBuilder parameter = new StringBuilder("{");
		if (parameterTypes != null) {
			for (Class<?> clazz : parameterTypes) {
				if (parameter.length() != 1) {
					parameter.append(KEY_PARAMS_SPLITE_SIGN);
				}

				parameter.append(clazz.getSimpleName());
			}
		}
		parameter.append("}");
		return parameter.toString();
	}

}
