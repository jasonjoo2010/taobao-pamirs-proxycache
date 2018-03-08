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
 * cacheproxy日志独立拆分到业务日志目录下
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
	        try { // 不能因为该类初始化失败导致其引用类初始化失败
	        	initLogFromBizLog();
	        } catch (Exception e) {
	        	log.error("cache proxy CaCheProxyLog error", e);
	            e.printStackTrace();
	        }
	    }


	    static private void initLogFromBizLog() {
	        // 使通信层的log4j配置生效(Logger, Appender)
	        DOMConfigurator.configure(CaCheProxyLog.class.getClassLoader().getResource("cache-proxy-log.xml"));
	        
	        /*
	         * 找到上层应用在Root Logger上设置的FileAppender，以及通信层配置的FileAppender。
	         * 目的是为了让通信层的日志与上层应用的日志输出到同一个目录。
	         */
	        FileAppender bizFileAppender = getFileAppender(Logger.getRootLogger());
	        if (null == bizFileAppender) {
	        	log.warn("上层业务层没有在ROOT LOGGER上设置FileAppender!!!");
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
		    appender.activateOptions(); // 很重要，否则原有日志内容会被清空
		    AsyncAppender asynAppender = new AsyncAppender();
		    asynAppender.addAppender(appender);
		    loggerLog4jImpl.addAppender(asynAppender);
		    loggerLog4jImpl.removeAppender(appender);
		    LogFactory.getLog(logName).warn("成功为"+logName+"添加Appender. 输出路径:" + filePath);
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
