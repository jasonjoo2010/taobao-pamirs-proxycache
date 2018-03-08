package com.taobao.pamirs.cache.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.taobao.pamirs.cache.framework.config.MethodConfig;

/**
 * ����Code������
 * 
 * @author xiaocheng 2012-11-2
 */
public class CacheCodeUtil {

	/**
	 * Key�����ָ���<br>
	 * ��ʽ��regionbeanName#methodName#{String}
	 */
	public static final String KEY_SPLITE_SIGN = "#";
	/**
	 * key�з��������ķָ���<br>
	 * ��ʽ��{String|Long}
	 */
	public static final String KEY_PARAMS_SPLITE_SIGN = "|";

	/** region�ָ��� */
	public static final String REGION_SPLITE_SIGN = "@";

	/**
	 * ȡ�����յĻ���Code�в���ֵ�ָ���<br>
	 * ��ʽ��regionbeanName#methodName#{String,Long}abc@@123
	 */
	public static final String CODE_PARAM_VALUES_SPLITE_SIGN = "@@";

	/**
	 * ȡ�����յĻ���Code<br>
	 * ��ʽ��region@beanName#methodName#{String|Long}abc@@123
	 * 
	 * @param region
	 * @param beanName
	 * @param methodConfig
	 * @param parameters
	 *            ���鳤�Ȼ���methodConfig.getParameterTypes()���ȣ�����Ļᶪʧ
	 * @return
	 */
	public static String getCacheCode(String region, String beanName,
			MethodConfig methodConfig, Object[] parameters) {
		// ���յĻ���code
		StringBuilder code = new StringBuilder();

		// 1. region
		// 2. bean + method + parameter
		code.append(getCacheAdapterKey(region, beanName, methodConfig));

		// 3. value
		List<Class<?>> parameterTypes = methodConfig.getParameterTypes();
		if (parameterTypes != null) {
			StringBuilder valus = new StringBuilder();
			for (int i = 0; i < parameterTypes.size(); i++) {
				if (valus.length() != 0) {
					valus.append(CODE_PARAM_VALUES_SPLITE_SIGN);
				}

				valus.append(parameters[i] == null ? "null" : parameters[i]
						.toString());
			}
			code.append(valus.toString());
		}

		return code.toString();
	}

	/**
	 * ������������key<br>
	 * ��ʽ��region@beanName#methodName#{String|Long}
	 * 
	 * @param region
	 * @param beanName
	 * @param methodConfig
	 * @return
	 */
	public static String getCacheAdapterKey(String region, String beanName,
			MethodConfig methodConfig) {
		Assert.notNull(methodConfig);

		// ���յ�key
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
	 * ����toString����ʽ{String|int}
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
