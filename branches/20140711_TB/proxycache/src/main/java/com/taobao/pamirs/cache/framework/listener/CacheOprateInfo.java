package com.taobao.pamirs.cache.framework.listener;

import java.io.Serializable;

import com.taobao.pamirs.cache.framework.CacheException;
import com.taobao.pamirs.cache.framework.config.MethodConfig;

/**
 * ��������������Ϣ
 * 
 * @author xiaocheng 2012-10-31
 */
public class CacheOprateInfo extends MethodConfig implements Serializable {

	//
	private static final long serialVersionUID = 7100282651039776916L;

	private String beanName;

	private Serializable key;
	private long methodTime;
	/** �Ƿ����У�for GET�� */
	private boolean isHitting;
	private CacheException cacheException;
	/** ������Դ��ip */
	private String ip;

	public CacheOprateInfo(Serializable key, long methodTime,
			boolean isHitting, String beanName, MethodConfig methodConfig,
			CacheException exception, String ip) {
		this.key = key;
		this.methodTime = methodTime;
		this.isHitting = isHitting;
		this.cacheException = exception;
		this.ip = ip;

		if (methodConfig != null) {
			this.setBeanName(beanName);
			this.setMethodName(methodConfig.getMethodName());
			this.setParameterTypes(methodConfig.getParameterTypes());
		}
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	/**
	 * ��������Ƿ�ɹ�
	 * 
	 * @return
	 */
	public boolean isSuccess() {
		return cacheException == null;
	}

	public Serializable getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isHitting() {
		return isHitting;
	}

	public long getMethodTime() {
		return methodTime;
	}

	public void setMethodTime(long methodTime) {
		this.methodTime = methodTime;
	}

	public CacheException getCacheException() {
		return cacheException;
	}

	public String getIp() {
		return ip == null ? "" : ip;
	}

}
