package com.taobao.pamirs.cache.framework.config;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.taobao.pamirs.cache.load.verify.Verfication;
import com.taobao.pamirs.cache.store.RemoveMode;
import com.taobao.pamirs.cache.util.IpUtil;

/**
 * 基本bean配置
 * 
 * @author xiaocheng 2012-11-2
 */
public class MethodConfig implements Serializable {

	//
	private static final long serialVersionUID = 1L;

	@Verfication(name = "方法名称", notEmpty = true)
	private String methodName;
	/**
	 * 参数类型
	 */
	@Verfication(name = "参数类型", notEmptyList = true)
	private List<Class<?>> parameterTypes;

	/**
	 * 失效时间，单位：秒。<br>
	 * 可以是相对时间，也可以是绝对时间(大于当前时间戳是绝对时间过期)。不传或0都是不过期 <br>
	 * 【可选项】
	 */
	private Integer expiredTime;
	
	
	/**
	 * put是否使用版本控制（目前tair支持，会多读一次缓存）
	 */
	private boolean useVersion=false;
	
	
	/**
	 * 该方法是否不走缓存
	 */
	private boolean isNotCache=false;
	
	/**
	 * 读不使用缓存的ip
	 */
	private String notCacheIps;
	
	
	
	/**
	 * 该方法失效缓存使用hidden方式，默认invaild（tair）
	 */
	private String removeMode=RemoveMode.INVAILD.getName();
	

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


	public String getRemoveMode() {
		return removeMode;
	}

	public void setRemoveMode(String removeMode) {
		this.removeMode = removeMode;
	}

	public String getNotCacheIps() {
		return notCacheIps;
	}

	public void setNotCacheIps(String notCacheIps) {
		this.notCacheIps = notCacheIps;
	}

	public boolean isNotCache() {
		return isNotCache;
	}

	public void setNotCache(boolean isNotCache) {
		this.isNotCache = isNotCache;
	}

	public boolean isLocalHostNotCache() {
		if(isNotCache){
			return true;
		}
		if(StringUtils.isNotBlank(notCacheIps)){
			String[] ips=this.notCacheIps.split(",");
			if(ips!=null&&ips.length>0){
				String local=IpUtil.getLocalIp();
				for(String ip:ips){
					if(ip.trim().equals(local)){
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isUseVersion() {
		return useVersion;
	}

	public void setUseVersion(boolean useVersion) {
		this.useVersion = useVersion;
	}


}
