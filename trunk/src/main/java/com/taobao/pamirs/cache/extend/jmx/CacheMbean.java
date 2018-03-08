package com.taobao.pamirs.cache.extend.jmx;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import com.taobao.pamirs.cache.extend.jmx.mbean.AbstractDynamicMBean;
import com.taobao.pamirs.cache.framework.CacheProxy;
import com.taobao.pamirs.cache.framework.config.CacheBean;
import com.taobao.pamirs.cache.framework.config.CacheConfig;
import com.taobao.pamirs.cache.framework.config.MethodConfig;
import com.taobao.pamirs.cache.util.AopProxyUtil;
import com.taobao.pamirs.cache.util.CacheCodeUtil;
import com.taobao.pamirs.cache.util.IpUtil;
import com.taobao.pamirs.cache.util.ParameterSupportTypeUtil;

/**
 * ����bean��Mbean
 * 
 * @author xuanyu
 * @author xiaocheng 2012-11-8
 */
public class CacheMbean<K extends Serializable, V extends Serializable> extends
		AbstractDynamicMBean {

	public static final String MBEAN_NAME = "Pamirs-Cache";

	private CacheProxy<K, V> cacheProxy = null;
	private CacheMbeanListener listener;
	private ApplicationContext applicationContext;
	/**
	 * ʧЧʱ�䣬��λ���롣
	 * 
	 * @see CacheBean.expiredTime
	 */
	private Integer expiredTime;
	/**
	 * Map�Զ�������ʽ
	 * 
	 * @see CacheConfig.storeMapCleanTime
	 */
	private String storeMapCleanTime;

	public CacheMbean(CacheProxy<K, V> cache, CacheMbeanListener listener,
			ApplicationContext applicationContext, String storeMapCleanTime,
			Integer expiredTime) {
		this.cacheProxy = cache;
		this.listener = listener;
		this.applicationContext = applicationContext;
		this.storeMapCleanTime = storeMapCleanTime;
		this.expiredTime = expiredTime;
	}

	public String getCacheName() {
		return cacheProxy.getKey();
	}

	public String getStoreType() {
		return cacheProxy.getStoreType().getName();
	}

	public String getStoreCount() {
		try {
			return cacheProxy.size() + "";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public boolean getIsUseCache() {
		return cacheProxy.isUseCache();
	}

	public long getReadHits() {
		return listener.getReadSuccessCount().get();
	}

	public long getReadUnHits() {
		return listener.getReadFailCount().get();
	}

	public String getReadHitRate() {
		return listener.getReadHitRate();
	}

	public long getReadAvgTime() {
		return listener.getReadAvgTime();
	}

	public long getWriteAvgTime() {
		return listener.getWriteAvgTime();
	}

	public long getRemoveCount() {
		return listener.getRemoveCount().get();
	}

	public long getExpireTime() {
		return expiredTime == null ? 0L : expiredTime.longValue();
	}

	public String getCleanTimeExpression() {
		return storeMapCleanTime;
	}

	@SuppressWarnings("unchecked")
	public V get(K key) {
		try {
			return cacheProxy.get((K) keyToCacheCode((String) key),
					IpUtil.getLocalIp());
		} catch (Exception e) {
			return (V) ("Cache Get Failure Key:" + key + " Exception:" + e
					.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public String put(K key, V value) {
		try {
			cacheProxy.put((K) keyToCacheCode((String) key), value,
					IpUtil.getLocalIp());
			return "Cache Put Successfully Key:" + key + " Value:" + value;
		} catch (Exception e) {
			return "Cache Put Failure Key:" + key + " Value:" + value
					+ " Exception:" + e.getMessage();
		}
	}

	@SuppressWarnings("unchecked")
	public String remove(K key) {
		try {
			cacheProxy.remove((K) keyToCacheCode((String) key),
					IpUtil.getLocalIp());
			return "Cache Remove Successfully Key:" + key;
		} catch (Exception e) {
			return "Cache Remove Failure Key:" + key;
		}
	}

	/**
	 * �������ͨ�� Cache ��ȡ��ʵֵ.
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public V getRealValue(K key) {
		try {
			// �������еķǷ�У��
			keyToCacheCode((String) key);

			MethodConfig methodConfig = cacheProxy.getMethodConfig();
			Object bean = AopProxyUtil
					.getPrimitiveProxyTarget(applicationContext
							.getBean(cacheProxy.getBeanName()));// ȡ��ԭ������
			List<Class<?>> parameterTypes = methodConfig.getParameterTypes();

			// �޲�
			if (parameterTypes == null) {
				Method method = bean.getClass().getMethod(
						methodConfig.getMethodName());
				return (V) method.invoke(bean);
			}

			// �в�
			Class<?>[] parameterTypeArray = new Class<?>[parameterTypes.size()];
			for (int i = 0; i < parameterTypeArray.length; i++) {
				parameterTypeArray[i] = parameterTypes.get(i);
			}
			Method method = bean.getClass().getMethod(
					methodConfig.getMethodName(), parameterTypeArray);

			String[] keyItems = key.toString().split(
					CacheCodeUtil.CODE_PARAM_VALUES_SPLITE_SIGN);
			Object[] parameterValues = new Object[parameterTypes.size()];
			for (int i = 0; i < parameterTypes.size(); i++) {
				parameterValues[i] = ParameterSupportTypeUtil
						.valueConvertToType(keyItems[i], parameterTypes.get(i));
			}
			return (V) method.invoke(bean, parameterValues);
		} catch (Exception e) {
			return (V) ("getRealValue Failure Key:" + key + " Exception:" + e
					.getMessage());
		}
	}

	public boolean getRealValueAndPut(K key) {
		V realValue = this.getRealValue(key);
		if (realValue != null)
			this.put(key, realValue);
		else
			this.remove(key);

		return true;
	}

	public boolean invalidBeforeCache() {
		cacheProxy.invalidBefore();
		return true;
	}

	private String keyToCacheCode(String key) throws Exception {
		Assert.notNull(key);

		MethodConfig methodConfig = cacheProxy.getMethodConfig();
		List<Class<?>> parameterTypes = methodConfig.getParameterTypes();

		String[] keyItems = key.toString().split(
				CacheCodeUtil.CODE_PARAM_VALUES_SPLITE_SIGN);

		boolean illegal = false;

		if (parameterTypes == null || parameterTypes.isEmpty()) {
			// 1. �޲η���
			if (StringUtils.isNotEmpty(key))
				illegal = true;
		} else {
			// 2. �вη���
			if (keyItems.length != parameterTypes.size())
				illegal = true;
		}

		if (illegal) {
			String erroMsg = "jmx�Ĳ��������ͽӿڵĲ���������һ��,����" + key.toString()
					+ "�ӿڲ���:" + parameterTypes;
			throw new RuntimeException(erroMsg);
		}

		// �޲�
		if (parameterTypes == null) {
			return CacheCodeUtil.getCacheCode(cacheProxy.getStoreRegion(),
					cacheProxy.getBeanName(), cacheProxy.getMethodConfig(),
					null);
		}

		// �в�
		Object[] parameterValues = new Object[parameterTypes.size()];
		for (int i = 0; i < parameterTypes.size(); i++) {
			parameterValues[i] = ParameterSupportTypeUtil.valueConvertToType(
					keyItems[i], parameterTypes.get(i));
		}

		return CacheCodeUtil.getCacheCode(cacheProxy.getStoreRegion(),
				cacheProxy.getBeanName(), cacheProxy.getMethodConfig(),
				parameterValues);
	}

	protected void buildDynamicMBeanInfo() {
		MBeanAttributeInfo[] dAttributes = new MBeanAttributeInfo[] {
				new MBeanAttributeInfo("cacheName", "String", "��������", true,
						false, false),
				new MBeanAttributeInfo("storeType", "String", "��������", true,
						false, false),
				new MBeanAttributeInfo("storeCount", "String", "����������", true,
						false, false),
				new MBeanAttributeInfo("isUseCache", "boolean", "�Ƿ�ʹ�û���", true,
						false, false),
				new MBeanAttributeInfo("readHits", "long", "�����д���", true,
						false, false),
				new MBeanAttributeInfo("readUnHits", "long", "��δ���д���", true,
						false, false),
				new MBeanAttributeInfo("readHitRate", "double", "����������", true,
						false, false),
				new MBeanAttributeInfo("readAvgTime", "long", "ƽ���������ʱ", true,
						false, false),
				new MBeanAttributeInfo("writeAvgTime", "long", "ƽ������д��ʱ", true,
						false, false),
				new MBeanAttributeInfo("removeCount", "long", "����ɾ������", true,
						false, false),
				new MBeanAttributeInfo("expireTime", "long", "��������ʧЧʱ��", true,
						false, false),
				new MBeanAttributeInfo("cleanTimeExpression", "String",
						"��������ʱ��", true, false, false) };

		String info = "�����Key����@@�ָ�";

		MBeanOperationInfo[] dOperations = new MBeanOperationInfo[] {
				new MBeanOperationInfo("get", "��ȡ����",
						new MBeanParameterInfo[] { new MBeanParameterInfo(
								"CacheGet", "java.lang.String", "���뻺��Key��"
										+ info) }, "String",
						MBeanOperationInfo.ACTION),
				new MBeanOperationInfo("put", "д�뻺��", new MBeanParameterInfo[] {
						new MBeanParameterInfo("CachePut Key",
								"java.lang.String", "���뻺��Key��" + info),
						new MBeanParameterInfo("CachePut Value",
								"java.lang.String", "���뻺��ֵValue.") }, "String",
						MBeanOperationInfo.ACTION),
				new MBeanOperationInfo("remove", "ɾ������",
						new MBeanParameterInfo[] { new MBeanParameterInfo(
								"CacheRemove", "java.lang.String", "���뻺��Key��"
										+ info) }, "String",
						MBeanOperationInfo.ACTION),
				new MBeanOperationInfo("getRealValue", "��ȡԭ�������������",
						new MBeanParameterInfo[] { new MBeanParameterInfo(
								"DiskGet", "java.lang.String", "���뷽������Key��"
										+ info) }, "String",
						MBeanOperationInfo.ACTION),
				new MBeanOperationInfo("invalidBeforeCache", "ʧЧ��ǰʱ��֮ǰ�洢����",
						null, "String", MBeanOperationInfo.ACTION),
				new MBeanOperationInfo("getRealValueAndPut",
						"��ȡԭ������������ݣ�����ֱ��Put��������",
						new MBeanParameterInfo[] { new MBeanParameterInfo(
								"DiskGetAndPut", "java.lang.String",
								"���뷽������Key��" + info) }, "boolean",
						MBeanOperationInfo.ACTION) };
		dMBeanInfo = new MBeanInfo(this.getClass().getName(), MBEAN_NAME,
				dAttributes, null, dOperations, null);
	}

}
