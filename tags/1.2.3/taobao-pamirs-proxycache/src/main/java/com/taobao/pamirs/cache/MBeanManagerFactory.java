package com.taobao.pamirs.cache;


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
import org.apache.commons.logging.LogFactory;
import org.springframework.core.JdkVersion;

public class MBeanManagerFactory {
	
	private static Log log = LogFactory.getLog(MBeanManagerFactory.class);
	
	/**
	 * ��ȡ���е�MBeanServer����ΪJDK��JBOSSʹ�ò�ͬ��MBeanServer
	 * ����ֵ��� �ճ���������ע�ᵽJDK��MBeanServer����Ԥ����������JBOSS��
	 * @return
	 * @throws MBeanRegistrationException
	 */
	public static ArrayList<MBeanServer> getMbeanServer()
			throws MBeanRegistrationException {
		if (JdkVersion.isAtLeastJava15()) {
			ArrayList<MBeanServer> mBeanServerAll = MBeanServerFactory.findMBeanServer(null);
			log.info("�� MBeanServerFactory �л�ȡ mbeanServer :" + mBeanServerAll);
			if(mBeanServerAll == null || mBeanServerAll.size()== 0){

				mBeanServerAll = new ArrayList<MBeanServer>();
				MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
				log.warn("�� ManagementFactory �л�ȡ mbeanServer :" + mbeanServer);
				if(mbeanServer==null){
					log.error("�޷����  mbeanServer factory=" + MBeanServerFactory.class);
					throw new MBeanRegistrationException(null, "�޷����  mbeanServer factory=" + MBeanServerFactory.class);					
				}				
				mBeanServerAll.add(mbeanServer);			
			}
			return mBeanServerAll;
			
		} else {
			throw new MBeanRegistrationException(null, "��ҪJDK1.5����");
		}

	}

	public static ObjectName registerMBean(String name,Object object)
			throws InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException, MalformedObjectNameException,
			NullPointerException {
		ObjectName result = new ObjectName(name);
		for(MBeanServer mBeanServer : getMbeanServer()){
			mBeanServer.registerMBean(object, result);
			log.info("registerMBean name=" + name +"; mbean="+object +"; server=" + mBeanServer);
		}
		return result;
	}

	public static void unregisterMBean(String name)
			throws InstanceNotFoundException, MBeanRegistrationException,
			MalformedObjectNameException, NullPointerException {
		for(MBeanServer mBeanServer : getMbeanServer()){
			mBeanServer.unregisterMBean(new ObjectName(name));
		}
	}
}
