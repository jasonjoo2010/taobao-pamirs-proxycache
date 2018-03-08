package com.taobao.pamirs.cache.framework.config;

import java.io.Serializable;
import java.util.List;

import com.taobao.pamirs.cache.load.verify.Verfication;

/**
 * ����bean����
 * 
 * @author xiaocheng 2012-11-2
 */
public class MethodConfig implements Serializable {

	//
	private static final long serialVersionUID = 1L;
	//ִ�е�bean��name
	private String beanName;
	
	@Verfication(name = "��������", notEmpty = true)
	private String methodName;
	/**
	 * ��������
	 */
	private List<Class<?>> parameterTypes;

	/**
	 * ʧЧʱ�䣬��λ���롣<br>
	 * ���������ʱ�䣬Ҳ�����Ǿ���ʱ��(���ڵ�ǰʱ����Ǿ���ʱ�����)��������0���ǲ����� <br>
	 * ����ѡ�
	 */
	private Integer expiredTime;
	/**
	 * ���ڵ����������������
	 */
	private String cleanTimeExp;
	/**
	 * �Ƿ��ⲿ����
	 */
	private boolean beRemoteCalled = false;
	/**
	 * �洢���ͣ��Է���������������
	 */
	private String storeType;

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * null: ����û��set,װ������ʱ��Ҫ���¸�ֵ <br>
	 * ��: �����޲η���
	 * 
	 * @return
	 */
	public List<Class<?>> getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(List<Class<?>> parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Integer getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(Integer expiredTime) {
		this.expiredTime = expiredTime;
	}

	public boolean isMe(String method, List<Class<?>> types) {
		if (!this.methodName.equals(method))
			return false;

		if (this.parameterTypes == null && types != null)
			return false;

		if (this.parameterTypes != null && types == null)
			return false;

		if (this.parameterTypes != null) {
			if (this.parameterTypes.size() != types.size())
				return false;

			for (int i = 0; i < parameterTypes.size(); i++) {
				if (!parameterTypes.get(i).getSimpleName()
						.equals(types.get(i).getSimpleName()))
					return false;
			}

		}

		return true;
	}

	public String getCleanTimeExp() {
		return cleanTimeExp;
	}

	public void setCleanTimeExp(String cleanTimeExp) {
		this.cleanTimeExp = cleanTimeExp;
	}

	public boolean isBeRemoteCalled() {
		return beRemoteCalled;
	}

	public void setBeRemoteCalled(boolean beRemoteCalled) {
		this.beRemoteCalled = beRemoteCalled;
	}

	public String getStoreType() {
		return storeType;
	}

	public void setStoreType(String storeType) {
		this.storeType = storeType;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

}
