package com.taobao.pamirs.cache.framework.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.taobao.pamirs.cache.load.verify.Verfication;

/**
 * ��������bean����
 * 
 * @author xiaocheng 2012-11-2
 */
public class CacheCleanBean implements Serializable {

	//
	private static final long serialVersionUID = -4582877908557906265L;

	@Verfication(name = "CacheCleanBean����", notEmpty = true)
	private String beanName;

	/**
	 * ��Ҫ�����ԭ�������б�
	 */
	@Verfication(name = "��Ҫ�����ԭ�������б�", notEmptyList = true)
	private List<CacheCleanMethod> methods;

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public List<CacheCleanMethod> getMethods() {
		if (methods == null)
			methods = new ArrayList<CacheCleanMethod>();
		return methods;
	}

	public void setMethods(List<CacheCleanMethod> methods) {
		this.methods = methods;
	}

}
