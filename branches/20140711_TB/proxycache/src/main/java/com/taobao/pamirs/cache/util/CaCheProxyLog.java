package com.taobao.pamirs.cache.util;

import java.io.File;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.AsyncAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * cacheproxy��־������ֵ�ҵ����־Ŀ¼��
 * @author tiebi.hlw
 *
 */
public class CaCheProxyLog {
	
		private static final Log log = LogFactory.getLog(CaCheProxyLog.class);
	
	 	static private final String LOGGER_NAME_TIMELOG = "cache-proxy-timelog-log";
	 	
	 	static private final String LOGGER_NAME_XRAY = "cache-proxy-xray-log";

	 	static private final String LOGGER_NAME_DEFAULT = "cache-proxy-log";

	    static public final Log LOGGER_TIMELOG = LogFactory.getLog(LOGGER_NAME_TIMELOG);

	    static public final Log LOGGER_XRAY = LogFactory.getLog(LOGGER_NAME_XRAY);

	    static public final Log LOGGER_DEFAULT = LogFactory.getLog(LOGGER_NAME_DEFAULT);

	    
	    static {
	        try { // ������Ϊ�����ʼ��ʧ�ܵ������������ʼ��ʧ��
	        	initLogFromBizLog();
	        } catch (Exception e) {
	        	log.error("cache proxy CaCheProxyLog error", e);
	            e.printStackTrace();
	        }
	    }


	    static private void initLogFromBizLog() {
	        // ʹͨ�Ų��log4j������Ч(Logger, Appender)
	        DOMConfigurator.configure(CaCheProxyLog.class.getClassLoader().getResource("cache-proxy-log.xml"));
	        
	        /*
	         * �ҵ��ϲ�Ӧ����Root Logger�����õ�FileAppender���Լ�ͨ�Ų����õ�FileAppender��
	         * Ŀ����Ϊ����ͨ�Ų����־���ϲ�Ӧ�õ���־�����ͬһ��Ŀ¼��
	         */
	        FileAppender bizFileAppender = getFileAppender(Logger.getRootLogger());
	        if (null == bizFileAppender) {
	        	log.warn("�ϲ�ҵ���û����ROOT LOGGER������FileAppender!!!");
	            return;
	        }
	        String bizLogDir = new File(bizFileAppender.getFile()).getParent();
	        
		    replaceAppender(LOGGER_NAME_DEFAULT,new File(bizLogDir, "cacheproxy_default.log").getAbsolutePath());
		    replaceAppender(LOGGER_NAME_TIMELOG,new File(bizLogDir, "cacheproxy_timelog.log").getAbsolutePath());
		    replaceAppender(LOGGER_NAME_XRAY,new File(bizLogDir, "cacheproxy_xray.log").getAbsolutePath());

	    }
	    
	    
	    static private void replaceAppender(String logName,String filePath){
	    	Logger loggerLog4jImpl = Logger.getLogger(logName);
		    FileAppender appender = getFileAppender(loggerLog4jImpl);
		    appender.setFile(filePath);
		    appender.activateOptions(); // ����Ҫ������ԭ����־���ݻᱻ���
		    AsyncAppender asynAppender = new AsyncAppender();
		    asynAppender.addAppender(appender);
		    loggerLog4jImpl.addAppender(asynAppender);
		    loggerLog4jImpl.removeAppender(appender);
		    LogFactory.getLog(logName).warn("�ɹ�Ϊ"+logName+"���Appender. ���·��:" + filePath);
	    }
	    

	    static private FileAppender getFileAppender(Logger logger) {
	        FileAppender fileAppender = null;
	        for (Enumeration<?> appenders = logger.getAllAppenders();
	                (null == fileAppender) && appenders.hasMoreElements();) {
	            Appender appender = (Appender) appenders.nextElement();
	            if (FileAppender.class.isInstance(appender)) {
	                fileAppender = (FileAppender) appender;
	            }
	        }
	        return fileAppender;
	    }


}
