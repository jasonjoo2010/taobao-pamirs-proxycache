package com.taobao.pamirs.cache.framework.config;

import java.util.List;

import com.taobao.pamirs.cache.load.verify.Verfication;

/**
 * ��������ķ������ڲ�����������clean methods
 * 
 * @author xiaocheng 2012-11-12
 */
public class CacheCleanMethod extends MethodConfig {

	//
	private static final long serialVersionUID = 2983763433924222529L;

	/**
	 * ��Ҫ����remove����ķ����б�
	 */
	@Verfication(name = "��Ҫ����remove����ķ����б�", notEmptyList = true)
	private List<MethodConfig> cleanMethods;

	public List<MethodConfig> getCleanMethods() {
		return cleanMethods;
	}

	public void setCleanMethods(List<MethodConfig> cleanMethods) {
		this.cleanMethods = cleanMethods;
	}

}
