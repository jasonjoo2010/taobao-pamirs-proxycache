package com.taobao.pamirs.cache.extend.timelog;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.List;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryAware;
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
		BeanFactoryAware, PriorityOrdered {

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
		super.setOrder(LOWEST_PRECEDENCE);
	}

	@Override
	protected Object[] getAdvicesAndAdvisorsForBean(
			@SuppressWarnings("rawtypes") Class beanClass, String beanName,
			TargetSource customTargetSource) throws BeansException {
		if (isAnnotationPresent(beanClass, TimeLog.class)
				|| (this.beanList != null && this.beanList.contains(beanName))) {

			if (log.isDebugEnabled()) {
				log.debug("������־����" + beanClass + ":" + beanName);
			}

			if (targetBeanIsFinal(beanClass)) {// must implements a interface
				this.setProxyTargetClass(false);// JDK
			} else {
				this.setProxyTargetClass(true);// CGLIB
			}

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

	private boolean targetBeanIsFinal(Class<?> clazz) {
		String inMods = Modifier.toString(clazz.getModifiers());
		if (inMods.contains("final")) {
			return true;
		} else {
			return false;
		}
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

class TimeAdvisor implements Advisor {

	TimeRoundAdvice advice;

	public TimeAdvisor(Class<?> aBeanClass, String beanName,
			TimeHandle timeHandle) {
		advice = new TimeRoundAdvice(aBeanClass, beanName, timeHandle);
	}

	@Override
	public Advice getAdvice() {
		return advice;
	}

	@Override
	public boolean isPerInstance() {
		return false;
	}

}

class TimeRoundAdvice implements Advice, MethodInterceptor {

	@SuppressWarnings("unused")
	private Class<?> beanClass;
	private TimeHandle timeHandle;
	private String beanName;

	public TimeRoundAdvice(Class<?> aBeanClass, String beanName,
			TimeHandle timeHandle) {
		this.beanClass = aBeanClass;
		this.timeHandle = timeHandle;
		this.beanName = beanName;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		long startTime = System.currentTimeMillis();

		String methodName = beanName + "$" + invocation.getMethod().getName();
		TimeLogManager.addCount();

		Object result = null;
		try {
			result = invocation.proceed();
		} finally {

			if (timeHandle.isOpenPrint()) {// ����
				// ��ӡ��ʽ
				StringBuilder sb = new StringBuilder();
				sb.append("[").append(methodName).append("]");

				if (timeHandle.isPrintParams()
						&& invocation.getArguments() != null
						&& invocation.getArguments().length != 0) {// ��ӡ����
					sb.append("(");
					sb.append(ToStringBuilder.reflectionToString(
							invocation.getArguments(),
							ToStringStyle.SIMPLE_STYLE));
					sb.append(")");
				}

				sb.append(":���� ")
						.append(System.currentTimeMillis() - startTime)
						.append(" ms");

				TimeLogManager.addLogInfo(sb.toString());
			} else {
				TimeLogManager.remove();
			}
		}

		return result;
	}
}
