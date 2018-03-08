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
	 * 获取所有的MBeanServer，因为JDK和JBOSS使用不同的MBeanServer
	 * 很奇怪的是 日常环境都是注册到JDK的MBeanServer，而预发和线上是JBOSS的
	 * @return
	 * @throws MBeanRegistrationException
	 */
	public static ArrayList<MBeanServer> getMbeanServer()
			throws MBeanRegistrationException {
		if (JdkVersion.isAtLeastJava15()) {
			ArrayList<MBeanServer> mBeanServerAll = MBeanServerFactory.findMBeanServer(null);
			log.info("从 MBeanServerFactory 中获取 mbeanServer :" + mBeanServerAll);
			if(mBeanServerAll == null || mBeanServerAll.size()== 0){

				mBeanServerAll = new ArrayList<MBeanServer>();
				MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
				log.warn("从 ManagementFactory 中获取 mbeanServer :" + mbeanServer);
				if(mbeanServer==null){
					log.error("无法获得  mbeanServer factory=" + MBeanServerFactory.class);
					throw new MBeanRegistrationException(null, "无法获得  mbeanServer factory=" + MBeanServerFactory.class);					
				}				
				mBeanServerAll.add(mbeanServer);			
			}
			return mBeanServerAll;
			
		} else {
			throw new MBeanRegistrationException(null, "需要JDK1.5以上");
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
