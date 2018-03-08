package com.taobao.pamirs.cache.framework.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.taobao.pamirs.cache.load.verify.Verfication;

/**
 * ����bean����
 * 
 * @author xiaocheng 2012-11-2
 */
public class CacheBean implements Serializable {

	//
	private static final long serialVersionUID = 4973185401294689002L;

	@Verfication(name = "CacheBean����", notEmpty = true)
	private String beanName;

	/**
	 * ����ķ����б�
	 */
	@Verfication(name = "����ķ����б�", notEmptyList = true)
	private List<MethodConfig> cacheMethods = new ArrayList<MethodConfig>();

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public List<MethodConfig> getCacheMethods() {
		return cacheMethods;
	}

	public void setCacheMethods(List<MethodConfig> cacheMethods) {
		this.cacheMethods = cacheMethods;
	}

}
