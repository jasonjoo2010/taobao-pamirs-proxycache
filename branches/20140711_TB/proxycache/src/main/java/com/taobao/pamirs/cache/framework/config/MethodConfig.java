package com.taobao.pamirs.cache.framework.config;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.taobao.pamirs.cache.load.verify.Verfication;
import com.taobao.pamirs.cache.store.RemoveMode;
import com.taobao.pamirs.cache.util.IpUtil;

/**
 * ����bean����
 * 
 * @author xiaocheng 2012-11-2
 */
public class MethodConfig implements Serializable {

	//
	private static final long serialVersionUID = 1L;

	@Verfication(name = "��������", notEmpty = true)
	private String methodName;
	/**
	 * ��������
	 */
	@Verfication(name = "��������", notEmptyList = true)
	private List<Class<?>> parameterTypes;

	/**
	 * ʧЧʱ�䣬��λ���롣<br>
	 * ���������ʱ�䣬Ҳ�����Ǿ���ʱ��(���ڵ�ǰʱ����Ǿ���ʱ�����)��������0���ǲ����� <br>
	 * ����ѡ�
	 */
	private Integer expiredTime;
	
	
	/**
	 * put�Ƿ�ʹ�ð汾���ƣ�Ŀǰtair֧�֣�����һ�λ��棩
	 */
	private boolean useVersion=false;
	
	
	/**
	 * �÷����Ƿ��߻���
	 */
	private boolean isNotCache=false;
	
	/**
	 * ����ʹ�û����ip
	 */
	private String notCacheIps;
	
	
	
	/**
	 * �÷���ʧЧ����ʹ��hidden��ʽ��Ĭ��invaild��tair��
	 */
	private String removeMode=RemoveMode.INVAILD.getName();
	

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
