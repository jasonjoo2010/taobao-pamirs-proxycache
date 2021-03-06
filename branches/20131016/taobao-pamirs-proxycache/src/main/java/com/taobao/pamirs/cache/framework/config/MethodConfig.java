package com.taobao.pamirs.cache.framework.config;

import java.io.Serializable;
import java.util.List;

import com.taobao.pamirs.cache.load.verify.Verfication;

/**
 * 基本bean配置
 * 
 * @author xiaocheng 2012-11-2
 */
public class MethodConfig implements Serializable {

	//
	private static final long serialVersionUID = 1L;
	//执行的bean的name
	private String beanName;
	
	@Verfication(name = "方法名称", notEmpty = true)
	private String methodName;
	/**
	 * 参数类型
	 */
	private List<Class<?>> parameterTypes;

	/**
	 * 失效时间，单位：秒。<br>
	 * 可以是相对时间，也可以是绝对时间(大于当前时间戳是绝对时间过期)。不传或0都是不过期 <br>
	 * 【可选项】
	 */
	private Integer expiredTime;
	/**
	 * 对于单个缓存的清理设置
	 */
	private String cleanTimeExp;
	/**
	 * 是否被外部调用
	 */
	private boolean beRemoteCalled = false;
	/**
	 * 存储类型，以方法的类型最优先
	 */
	private String storeType;

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * null: 代表没有set,装载配置时需要重新赋值 <br>
	 * 空: 代表无参方法
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
