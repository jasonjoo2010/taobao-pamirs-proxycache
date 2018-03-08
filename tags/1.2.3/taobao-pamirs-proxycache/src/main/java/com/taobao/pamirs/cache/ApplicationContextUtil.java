package com.taobao.pamirs.cache;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextUtil implements ApplicationContextAware{

	public static ApplicationContext APPLICATIONCONTEXT;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		APPLICATIONCONTEXT = applicationContext;
	}
	public static ApplicationContext getApplicationContext(){
		return APPLICATIONCONTEXT;
	}
	public static Object getBean(String beanName){
		return APPLICATIONCONTEXT.getBean(beanName);
	}
}
