package com.taobao.pamirs.cache.timer;

import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.pamirs.cache.cache.Cache;

public class TimeTaskManager {
	private Timer timer;
	private Map<String,CacheManagerTimerTask> taskMap = new ConcurrentHashMap<String,CacheManagerTimerTask>();
	
	public TimeTaskManager(){
		timer = new Timer("CacheManagerTimeTaskManager");
	}
	public void createCleanCacheTask(Cache<String,Object> Cache,String aCronTabExpress) throws Exception{
		CronExpression cexp = new CronExpression(aCronTabExpress);
		Date current = new Date(System.currentTimeMillis());
		Date nextTime = cexp.getNextValidTimeAfter(current);
		CacheManagerTimerTask task = new CacheManagerTimerTask(this,Cache,aCronTabExpress);
		taskMap.put(Cache.getCacheName(), task);
		this.timer.schedule(task, nextTime);
	}
	public void removeCleanCacheTask(String cacheName){
		CacheManagerTimerTask task = this.taskMap.remove(cacheName);
		task.cancel();
	}
}

class  CacheManagerTimerTask extends TimerTask{
	private static transient Log log = LogFactory.getLog(CacheManagerTimerTask.class);
	Cache<String,Object> Cache;
	String cronTabExpress;
	TimeTaskManager manager;
	public CacheManagerTimerTask(TimeTaskManager aManager,Cache<String,Object> aCache,String aCronTabExpress){
		this.manager = aManager;
		this.Cache = aCache;
		this.cronTabExpress = aCronTabExpress;
	}
	public void run() {
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		this.manager.removeCleanCacheTask(this.Cache.getCacheName());
		try{
			this.Cache.clear();
		}finally{
			try{
				this.manager.createCleanCacheTask(this.Cache,this.cronTabExpress);
			}catch(Exception e){
				log.fatal("严重错误，定时器处理失败：" + e.getMessage(),e);
			}

		}
	}
}

class CleanCacheTask implements Runnable{
	Cache<String,Object> item;
	CleanCacheTask(Cache<String,Object> aItem){
		this.item =aItem;
	}
	public void run() {
		 this.item.clear();	
	}	
}