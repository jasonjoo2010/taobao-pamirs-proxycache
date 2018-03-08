package com.taobao.pamirs.cachemanager.test;

import java.io.Serializable;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.taobao.notify.message.ObjectMessage;
import com.taobao.notify.remotingclient.NotifyManagerBean;
import com.taobao.pamirs.cache.aop.handle.CacheManagerHandle;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;
import com.taobao.tair.TairManager;

/**
 * 
 * @author xuannan
 * 
 */

@SpringApplicationContext({ "CacheManager.xml"})
public class SpringTest extends UnitilsJUnit4 {
	
	@SpringBeanByName
	CacheManagerHandle cacheManagerHandle;
	public void setCacheManagerHandle(CacheManagerHandle cacheManagerHandle) {
		this.cacheManagerHandle = cacheManagerHandle;
	}
	
	@SpringBeanByName
	TestBeanImpl testBean;
	public void setTestBean(TestBeanImpl testBean) {
		this.testBean = testBean;
	}
	
	@SpringBeanByName
	TairManager tairManager;
	public void setTairManager(TairManager tairManager){
		this.tairManager = tairManager;
	}
	
//	@SpringBeanByName
//	NotifyManagerBean notifyManagerBean;
//	public void setNotifyManagerBean(NotifyManagerBean notifyManagerBean){
//		this.notifyManagerBean = notifyManagerBean;
//	}
	
//	@SpringBeanByName
//	NotifyManagerBean notifyManager;
//	public void setNotifyManager(NotifyManagerBean notifyManager){
//		this.notifyManager = notifyManager;
//	}	
	
//	@Test 	
//	public void testNotify(){
//
//		System.out.println("notifyManager" + notifyManager);
//		
//		for (int i = 0; i < 100; i++) {
//			ObjectMessage notifyMessage = new ObjectMessage();
//			notifyMessage.setTopic("UPP-SERVICE");
//			notifyMessage.setMessageType("service-open");
//			notifyMessage.setObject("Hello");
//			
//			notifyManager.sendMessage(notifyMessage);
//		}
//
//	}
	
	@Test
	public void testCacheManager() throws InterruptedException {
		
			System.out.println(testBean.getService(5));
			System.out.println(testBean.getService(5));
			testBean.updateService(100,"dddddddd");
			System.out.println(testBean.getService(5));
			System.out.println(testBean.getService(5));
			System.out.println(testBean.getService(100));
			System.out.println(testBean.getProduct(100));
			System.out.println(testBean.getSp(1000));

			System.out.println(testBean.getService(100));
			testBean.deleteService();
			System.out.println(testBean.getService(5));
			System.out.println(testBean.getProduct(100));
	}
	@Test
	public void testTairManager() throws InterruptedException {		
		 tairManager.invalid(136, "");
		 Result<DataEntry> result = tairManager.get(136, "");
		 ResultCode rc = tairManager.put(136, "",(Serializable) "", 0, 600000);
	}	
}