package com.taobao.pamirs.cache.extend.timelog;

import java.lang.annotation.Annotation;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.BeansException;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

import com.taobao.pamirs.cache.extend.jmx.annotation.JmxClass;
import com.taobao.pamirs.cache.extend.jmx.annotation.JmxMethod;
import com.taobao.pamirs.cache.extend.timelog.annotation.TimeLog;

/**
 * ����ʱ����־��ӡ������ auto proxy
 * 
 * @author xiaocheng 2012-8-24
 */
@JmxClass
@Component("timeHandle")
public class TimeHandle extends AbstractAutoProxyCreator implements
		PriorityOrdered {

	private static final Log log = LogFactory.getLog(TimeHandle.class);

	/**  */
	private static final long serialVersionUID = 1L;

	/** ��־��ӡ���أ�Ĭ�Ϲر���־��ӡ���������� */
	private boolean openPrint = false;

	/** �Ƿ��ӡ����������Ĭ�Ϲر� */
	private boolean printParams = false;

	/**
	 * ������--ע�����һ��ѡ��ʽ
	 */
	private List<String> beanList;

	public void setBeanList(List<String> beanList) {
		this.beanList = beanList;
	}

	public TimeHandle() {
		this.setOrder(LOWEST_PRECEDENCE);
		this.setProxyTargetClass(true);
		this.setExposeProxy(true);// do call another advised method on itself
	}

	@Override
	protected Object[] getAdvicesAndAdvisorsForBean(
			@SuppressWarnings("rawtypes") Class beanClass, String beanName,
			TargetSource customTargetSource) throws BeansException {
		if (isAnnotationPresent(beanClass, TimeLog.class)
				|| (this.beanList != null && this.beanList.contains(beanName))) {

			if (log.isDebugEnabled())
				log.debug("������־����" + beanClass + ":" + beanName);

			return new TimeAdvisor[] { new TimeAdvisor(beanClass, beanName,
					this) };
		}

		return DO_NOT_PROXY;
	}

	private boolean isAnnotationPresent(Class<?> aClass,
			Class<? extends Annotation> annotationClass) {
		while (aClass != null) {
			if (aClass.isAnnotationPresent(annotationClass)) {
				return true;
			}

			for (Class<?> interfaceClass : aClass.getInterfaces()) {
				if (interfaceClass.isAnnotationPresent(annotationClass)) {
					return true;
				}
			}

			aClass = aClass.getSuperclass();
		}
		return false;
	}

	@JmxMethod
	public void setOpenPrint(boolean openPrint) {
		this.openPrint = openPrint;
	}

	@JmxMethod
	public void setPrintParams(boolean printParams) {
		this.printParams = printParams;
	}

	public boolean isOpenPrint() {
		return openPrint;
	}

	public boolean isPrintParams() {
		return printParams;
	}

}