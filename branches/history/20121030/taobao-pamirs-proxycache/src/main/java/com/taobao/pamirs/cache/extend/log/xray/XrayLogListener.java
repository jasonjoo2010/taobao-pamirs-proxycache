package com.taobao.pamirs.cache.extend.log.xray;

import java.util.List;

import com.taobao.pamirs.cache.framework.listener.CacheOprateInfo;
import com.taobao.pamirs.cache.framework.listener.CacheOprateListener;
import com.taobao.pamirs.cache.framework.listener.CacheOprator;
import com.taobao.pamirs.cache.util.CacheCodeUtil;
import com.taobao.pamirs.cache.util.asynlog.AsynWriter;

/**
 * 打印给xray统计
 * 
 * @author xiaocheng 2012-11-13
 */
public class XrayLogListener implements CacheOprateListener {

	private AsynWriter<String> writer = new AsynWriter<String>();

	private String beanName;
	private String methodName;
	private List<Class<?>> parameterTypes;

	/** 关键字 */
	private static final String XRAY_KEYWORD = "PAMIRS_CACHE_XRAY";
	private static final String SEPARATOR = ",";

	public XrayLogListener(String beanName, String methodName,
			List<Class<?>> parameterTypes) {
		this.beanName = beanName;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
	}

	@Override
	public void oprate(CacheOprator oprator, CacheOprateInfo cacheInfo) {
		writer.write(getXrayLog(oprator, cacheInfo.isHitting(),
				cacheInfo.getMethodTime(), cacheInfo.getIp()));
	}

	/**
	 * Xray日志格式
	 * 
	 * @param type
	 * @param isHit
	 * @param useTime
	 * @param parameterTypes
	 */
	private String getXrayLog(CacheOprator type, boolean isHit, long useTime,
			String ip) {
		StringBuilder sb = new StringBuilder();
		sb.append(SEPARATOR).append(XRAY_KEYWORD);
		sb.append(SEPARATOR).append(ip);
		sb.append(SEPARATOR).append(beanName);
		sb.append(SEPARATOR).append(methodName);
		sb.append(SEPARATOR).append(
				CacheCodeUtil.parameterTypesToString(parameterTypes));
		sb.append(SEPARATOR).append(type.name());
		sb.append(SEPARATOR).append(isHit);
		sb.append(SEPARATOR).append(useTime);

		return sb.toString();
	}

}