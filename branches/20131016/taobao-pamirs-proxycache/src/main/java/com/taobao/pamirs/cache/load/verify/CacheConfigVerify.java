package com.taobao.pamirs.cache.load.verify;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.taobao.pamirs.cache.framework.config.CacheBean;
import com.taobao.pamirs.cache.framework.config.CacheCleanBean;
import com.taobao.pamirs.cache.framework.config.CacheCleanMethod;
import com.taobao.pamirs.cache.framework.config.CacheConfig;
import com.taobao.pamirs.cache.framework.config.MethodConfig;
import com.taobao.pamirs.cache.load.LoadConfigException;
import com.taobao.pamirs.cache.util.CacheCodeUtil;

/**
 * �������úϷ���У��
 * 
 * <pre>
 * 	  ����У�����ݣ�
 * 		1������ؼ����þ�̬У��, @see {@link Verfication}
 * 		2�����淽���Ƿ�����ظ�����У��
 * 		3�������������Ƿ�����ظ�����У��
 * 		4�������������Ĺ��������Ƿ�����ظ�����У��
 * 		5�����淽��������Spring�е���Ҫ���ڣ����ҺϷ�
 * 		6��������������Spring����Ҫ���ڣ����ҺϷ�
 * </pre>
 * 
 * @author poxiao.gj
 * @author xiaocheng 2012-11-29
 */
public class CacheConfigVerify {

	/**
	 * У�黺������
	 * 
	 * @param applicationContext
	 * @param cacheConfig
	 * @throws LoadConfigException
	 */
	public static void checkCacheConfig(CacheConfig cacheConfig,
			ApplicationContext applicationContext) throws LoadConfigException {
		Assert.notNull(applicationContext);
		Assert.notNull(cacheConfig);
		Assert.isTrue(!CollectionUtils.isEmpty(cacheConfig.getCacheBeans())
				|| !CollectionUtils.isEmpty(cacheConfig.getCacheBeans()),
				"�����л���������治��ͬʱΪ�գ�");

		// 1. ��̬У��
		try {
			StaticCheck.check(cacheConfig);

			if (cacheConfig.getCacheBeans() != null) {
				for (CacheBean bean : cacheConfig.getCacheBeans()) {
					StaticCheck.check(bean);

					if (bean.getCacheMethods() != null) {
						for (MethodConfig method : bean.getCacheMethods())
							StaticCheck.check(method);
					}
				}
			}

			if (cacheConfig.getCacheCleanBeans() != null) {
				for (CacheCleanBean bean : cacheConfig.getCacheCleanBeans()) {
					StaticCheck.check(bean);

					if (bean.getMethods() != null) {
						for (CacheCleanMethod method : bean.getMethods()) {
							StaticCheck.check(method);

							for (MethodConfig subMethod : method
									.getCleanMethods()) {
								StaticCheck.check(subMethod);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new LoadConfigException(e.getMessage());
		}

		// 2. ��̬SpringУ��
		if (cacheConfig.getCacheBeans() != null) {
			for (CacheBean cacheBean : cacheConfig.getCacheBeans()) {
				for (MethodConfig methodConfig : cacheBean.getCacheMethods()) {
					doValidSpringMethod(applicationContext,
							cacheBean.getBeanName(),
							methodConfig.getMethodName(),
							methodConfig.getParameterTypes());
				}
			}
		}

		if (cacheConfig.getCacheCleanBeans() != null) {
			for (CacheCleanBean cleanBean : cacheConfig.getCacheCleanBeans()) {
				for (CacheCleanMethod method : cleanBean.getMethods()) {
					doValidSpringMethod(applicationContext,
							cleanBean.getBeanName(), method.getMethodName(),
							method.getParameterTypes());

					for (MethodConfig clearMethod : method.getCleanMethods()) {
						doValidSpringMethod(applicationContext,
								cleanBean.getBeanName(),
								clearMethod.getMethodName(),
								clearMethod.getParameterTypes());
					}
				}
			}
		}

		// 3. �����ظ�У��
		checkRepeatMethod(cacheConfig);
	}

	/**
	 * У�����õ�method�Ƿ����
	 * 
	 * @param applicationContext
	 * @param beanName
	 * @param methodName
	 * @param parameterTypes
	 */
	private static void doValidSpringMethod(
			ApplicationContext applicationContext, String beanName,
			String methodName, List<Class<?>> parameterTypes) {
		Assert.notNull(applicationContext);
		Assert.notNull(beanName);
		Assert.notNull(methodName);
		Assert.notNull(parameterTypes);// autoFillʱ������������䣬null�ᱻ���Ϊ��List

		Object bean = applicationContext.getBean(beanName);
		Assert.notNull(bean, "�Ҳ���Bean:" + beanName);

		Method[] methods = bean.getClass().getMethods();

		boolean isOk = false;

		for (Method m : methods) {
			if (m.getName().equals(methodName)) {
				// ��������ҲҪһ��
				Class<?>[] toCompareParams = m.getParameterTypes();

				if (toCompareParams.length != parameterTypes.size())
					continue;

				boolean haveDiff = false;

				for (int i = 0; i < toCompareParams.length; i++) {
					if (!toCompareParams[i].equals(parameterTypes.get(i))) {
						haveDiff = true;
						break;
					}
				}

				if (!haveDiff) {
					isOk = true;
					break;
				}
			}
		}

		if (!isOk) {
			throw new LoadConfigException("�Ҳ������õķ���,Bean=" + beanName
					+ ",method=" + methodName + ",params=" + parameterTypes.toString());
		}
	}

	private static void checkRepeatMethod(CacheConfig cacheConfig) {
		// 3.1 ���淽���Ƿ�����ظ�����У��
		List<String> keys = new ArrayList<String>();
		if (cacheConfig.getCacheBeans() != null) {
			for (CacheBean cacheBean : cacheConfig.getCacheBeans()) {
				for (MethodConfig methodConfig : cacheBean.getCacheMethods()) {
					String cacheAdapterKey = CacheCodeUtil.getCacheAdapterKey(
							cacheConfig.getStoreRegion(),
							cacheBean.getBeanName(), methodConfig);

					if (keys.contains(cacheAdapterKey))
						throw new LoadConfigException("���������з����ظ���,Bean:"
								+ cacheBean.getBeanName() + ",method="
								+ methodConfig.getMethodName());

					keys.add(cacheAdapterKey);
				}
			}
		}

		// 3.2 �����������Ƿ�����ظ�����У��
		keys.clear();
		if (cacheConfig.getCacheCleanBeans() != null) {
			for (CacheCleanBean cleanBean : cacheConfig.getCacheCleanBeans()) {
				for (CacheCleanMethod method : cleanBean.getMethods()) {
					String cacheAdapterKey = CacheCodeUtil.getCacheAdapterKey(
							cacheConfig.getStoreRegion(),
							cleanBean.getBeanName(), method);

					if (keys.contains(cacheAdapterKey))
						throw new LoadConfigException("�������������з����ظ���,Bean:"
								+ cleanBean.getBeanName() + ",method="
								+ method.getMethodName());

					keys.add(cacheAdapterKey);
				}
			}
		}

		// 3.3 �����������Ĺ��������Ƿ�����ظ�����У��
		keys.clear();
		if (cacheConfig.getCacheCleanBeans() != null) {
			for (CacheCleanBean cleanBean : cacheConfig.getCacheCleanBeans()) {
				for (CacheCleanMethod method : cleanBean.getMethods()) {
					for (MethodConfig clearMethod : method.getCleanMethods()) {
						String cacheAdapterKey = CacheCodeUtil
								.getCacheAdapterKey(
										cacheConfig.getStoreRegion(),
										cleanBean.getBeanName(), clearMethod);

						if (keys.contains(cacheAdapterKey))
							throw new LoadConfigException(
									"��������������������з����ظ���,Bean:"
											+ cleanBean.getBeanName()
											+ ",method="
											+ method.getMethodName()
											+ ",clearMethod="
											+ clearMethod.getMethodName());

						keys.add(cacheAdapterKey);
					}
				}
			}
		}
	}

}
