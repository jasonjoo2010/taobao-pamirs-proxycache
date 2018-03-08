package com.taobao.notify.sample.test;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.taobao.notify.message.ObjectMessage;
import com.taobao.notify.remotingclient.NotifyManagerBean;

/**
 * 
 * @author xuannan
 * 
 */

@SpringApplicationContext({ "CacheNotify.xml" })
public class NotifyTest extends UnitilsJUnit4 {
	
	
	@SpringBeanByName
	NotifyManagerBean notifyManager;
	public void setNotifyManager(NotifyManagerBean notifyManager){
		this.notifyManager = notifyManager;
	}	
	
	@Test 	
	public void testNotify(){

		System.out.println("notifyManager" + notifyManager);
		
		for (int i = 0; i < 100; i++) {
			ObjectMessage notifyMessage = new ObjectMessage();
			notifyMessage.setTopic("UPP-SERVICE");
			notifyMessage.setMessageType("service-open");
			notifyMessage.setObject("Hello");
			
			notifyManager.sendMessage(notifyMessage);
		}

	}
}