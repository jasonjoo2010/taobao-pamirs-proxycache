package com.taobao.pamirs.cache.extend.jmx.mbean;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.springframework.core.JdkVersion;

import com.taobao.pamirs.cache.util.CaCheProxyLog;

/**
 * Mbean������
 * 
 * @author xuanyu
 * @author xiaocheng 2012-11-8
 */
public class MBeanManagerFactory {

	private static final Log log = CaCheProxyLog.LOGGER_DEFAULT;

	/**
	 * ��ȡ���е�MBeanServer����ΪJDK��JBOSSʹ�ò�ͬ��MBeanServer ����ֵ���
	 * �ճ���������ע�ᵽJDK��MBeanServer����Ԥ����������JBOSS��
	 * 
	 * @return
	 * @throws MBeanRegistrationException
	 */
	public static ArrayList<MBeanServer> getMbeanServer()
			throws MBeanRegistrationException {
		if (!JdkVersion.isAtLeastJava15())
			throw new MBeanRegistrationException(null, "��ҪJDK1.5����");

		//
		ArrayList<MBeanServer> mBeanServerAll = MBeanServerFactory
				.findMBeanServer(null);
		if (mBeanServerAll != null && !mBeanServerAll.isEmpty()) {
			log.info("�� MBeanServerFactory �л�ȡ mbeanServer");
			return mBeanServerAll;
		}

		mBeanServerAll = new ArrayList<MBeanServer>();
		MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
		if (mbeanServer == null) {
			String errMsg = "�޷����  mbeanServer factory="
					+ MBeanServerFactory.class;
			log.error(errMsg);
			throw new MBeanRegistrationException(null, errMsg);
		}

		mBeanServerAll.add(mbeanServer);
		return mBeanServerAll;
	}

	public static ObjectName registerMBean(String name, Object object)
			throws InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException, MalformedObjectNameException,
			NullPointerException {
		ObjectName result = new ObjectName(name);
		for (MBeanServer mBeanServer : getMbeanServer()) {
			mBeanServer.registerMBean(object, result);
			log.info("registerMBean name=" + name + "; mbean=" + object
					+ "; server=" + mBeanServer);
		}
		return result;
	}

	public static void unregisterMBean(String name)
			throws InstanceNotFoundException, MBeanRegistrationException,
			MalformedObjectNameException, NullPointerException {
		for (MBeanServer mBeanServer : getMbeanServer()) {
			mBeanServer.unregisterMBean(new ObjectName(name));
		}
	}
}
