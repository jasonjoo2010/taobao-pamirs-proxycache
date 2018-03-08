package com.taobao.pamirs.cache.cache;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.pamirs.cache.config.BeanCacheConfig;
import com.taobao.pamirs.cache.manager.CacheManager;
import com.taobao.pamirs.cache.store.Store;
import com.taobao.pamirs.cache.store.StoreObject;
import com.taobao.pamirs.cache.store.map.MapStore;
import com.taobao.pamirs.cache.store.tair.TairCacheStore;

public class Cache<K,V> {
	private static transient Log log = LogFactory.getLog(Cache.class);
    /**
     * 缓存配置
     */
	BeanCacheConfig cacheConfig;
	
	public BeanCacheConfig getBeanCacheConfig(){
		return cacheConfig;
	}
	public String getCacheInfo(){
		return cacheConfig.getBeanName()+"."+cacheConfig.getMethodName();
	}
	
	/**
	 * 是否使用缓存
	 */
	boolean isUseCache = true;
	/**
	 * 读取次数
	 */
	AtomicLong succesCount = new AtomicLong(0);
	/**
	 * 命中失败次数
	 */
	AtomicLong failCount =new AtomicLong(0);
	//缓存数据读取时间.
	AtomicLong readTime = new AtomicLong(0);
	//缓存数据存入时间.
	AtomicLong writeTime = new AtomicLong(0);
	//缓存写入次数.
	AtomicLong writeCount = new AtomicLong(0);	
	/**
	 * 清理次数
	 */
	AtomicLong removeCount = new AtomicLong(0);
	
	//判断统计数据清空上限.
	final long MAX = Long.MAX_VALUE - 1000000;
	
			
	//缓存命中率
	public String getHitRate() {
		
		if(this.succesCount == null || this.failCount == null){
			log.warn("Cache getHitRate warn succesCount=" + succesCount+";failCount="+failCount );
			return "HitRate Error";
		}
		
		long allCount =  this.succesCount.get()+ this.failCount.get();
		if(allCount != 0){
			BigDecimal hitRated = new BigDecimal( this.succesCount.get() * 100 /allCount );
			return hitRated.toString() + "%";
		}
		return "HitRate Error";
	}
		
	private Store<K,StoreObject<K,V>> store = null;

	public Cache(BeanCacheConfig aCacheConfig) {
		this.cacheConfig = aCacheConfig;

		if(Store.STORE_TYPE_MAP.equalsIgnoreCase(this.cacheConfig.getStoreType())){
			store = new MapStore<K,StoreObject<K,V>>();
		}else if(Store.STORE_TYPE_TAIR.equalsIgnoreCase(this.cacheConfig.getStoreType())){
			store = new TairCacheStore<K,StoreObject<K,V>>(
					this.cacheConfig.getCacheCode(),
					this.cacheConfig.getStoreTairRegion(),
					this.cacheConfig.getStoreTairNameSpace());
		}else{
			throw new RuntimeException("不支持的存储类型：" + this.cacheConfig.getStoreType() + " of " + this.cacheConfig.getCacheName());
		}
	}
	/**
	 清空统计数据.
	 * **/
	private void clearStatistics(){
		this.readTime.set(0);
		this.succesCount.set(0);
		this.failCount.set(0);
		this.writeTime.set(0);
		this.writeCount.set(0);
		this.removeCount.set(0);
	}
	public V get(K key) {
		
		if( !this.isUseCache ){
			return null;
		}
		// 将统计数据清空
		if( this.readTime.longValue() > MAX || this.succesCount.longValue() > MAX ){
			this.clearStatistics();
		}
		
		StoreObject<K,V> storeObject = null;
		
		try {
			long d1 = System.currentTimeMillis();
			storeObject = store.get(key);
			long d2 = System.currentTimeMillis();
			
			this.readTime.addAndGet(d2-d1);
		} catch (Exception e) {
			log.warn("Store 获取存储对象异常: " + key);
			return null;
		}
	
		if(storeObject == null){
			failCount.incrementAndGet();
			return null;
		}else{
			this.succesCount.incrementAndGet();
		}
		
		V value = null;	
		//有效时间判断
		if( this.cacheConfig.getExpireTimes() > 0){
			if( System.currentTimeMillis() - storeObject.getTimestamp() < this.cacheConfig.getExpireTimes() ){
				value = storeObject.getObject();
			}
		}else{
			value = storeObject.getObject();
		}
		
		return value;
	}
	public void put(K key, V value) {
		
		try {
			StoreObject<K,V> co = new StoreObject<K,V>(value);		
			
			long d1 = System.currentTimeMillis();
			store.put(key, co);
			long d2 = System.currentTimeMillis();
			
			this.writeTime.addAndGet(d2 - d1);
			this.writeCount.incrementAndGet();
			
		} catch (Exception e) {
			log.warn("Store Put存储对象异常: " + key);
		}	
	}
	public void remove(K key){
		try {
			log.debug("清除缓存数据: cacheName=" + this.cacheConfig.getCacheName() + ",key=" + key );
			store.remove(key);
			this.removeCount.incrementAndGet();
		} catch (Exception e) {
			log.warn("Store Remove存储对象异常: " + key);
		}

	}
	public String clear() {
		String result = null;
		try {
			store.clear();
			if(log.isInfoEnabled()){
				log.info("清除缓缓存成功: cacheName=" + this.cacheConfig.getCacheName());
			}
			result = "清除缓存成功 " + this.cacheConfig.getCacheName() ;
		} catch (Exception e) {
			log.warn("Store clear存储对象异常: " + e.getMessage());
			result = "清除缓存失败 " + this.cacheConfig.getCacheName() ;
		}
		return result;
	}
	public long getSuccesCount(){
		return this.succesCount.get();
	}
	public long getFailCount(){
		return this.failCount.get();
	}
	public long getRemoveCount(){
		return this.removeCount.get();
	}

	public String reloadData() {	
		return null;
	}

	public String getCacheName() {
		return this.cacheConfig.getCacheName();
	}

	public boolean getIsUseCache() {
		return isUseCache;
	}
	public void setIsUseCache(boolean isUseCache) {
		this.isUseCache = isUseCache;
	}
	
	public long getAvagReadTime() {
		
		long succesCountLong = succesCount.longValue();
		long failCountLong = failCount.longValue();
		
		if(succesCountLong + failCountLong!= 0L){		
			return readTime.longValue()/(succesCountLong + failCountLong);
		}
		return 0L;
	}
	public long getExpireTimes() {
		return this.cacheConfig.getExpireTimes();
	}
	public long getAvagWriteTime() {
		
		long writeCountLong = writeCount.longValue();
		
		if(writeCountLong != 0L){
			return writeTime.longValue()/writeCountLong;
		}
		return 0L;
	}
	@SuppressWarnings("unchecked")
	public String setCleanTime(String cleanTime) throws Exception {
		CacheManager.getTimeTaskManager().removeCleanCacheTask(this.cacheConfig.getCacheName());
		if(this.cacheConfig.getCleanTime() != null){
			CacheManager.getTimeTaskManager().createCleanCacheTask((Cache<String, Object>) this,cleanTime);
		}
		return "设置清除时间成功";
	}

	
	public String getDataCount() {
		 if(this.cacheConfig.getStoreType().equals(Store.STORE_TYPE_TAIR)){
			 return "TairStore don't support";
		 }else{
			 return this.store.size() + "";
		 }
	}
	public String getStoreType() {
		return this.cacheConfig.getStoreType();
	}
	public String getCleanTime(){
		return this.cacheConfig.getCleanTime();
	}
   
}
