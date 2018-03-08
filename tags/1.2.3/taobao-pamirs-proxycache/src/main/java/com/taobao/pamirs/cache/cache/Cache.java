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
     * ��������
     */
	BeanCacheConfig cacheConfig;
	
	public BeanCacheConfig getBeanCacheConfig(){
		return cacheConfig;
	}
	public String getCacheInfo(){
		return cacheConfig.getBeanName()+"."+cacheConfig.getMethodName();
	}
	
	/**
	 * �Ƿ�ʹ�û���
	 */
	boolean isUseCache = true;
	/**
	 * ��ȡ����
	 */
	AtomicLong succesCount = new AtomicLong(0);
	/**
	 * ����ʧ�ܴ���
	 */
	AtomicLong failCount =new AtomicLong(0);
	//�������ݶ�ȡʱ��.
	AtomicLong readTime = new AtomicLong(0);
	//�������ݴ���ʱ��.
	AtomicLong writeTime = new AtomicLong(0);
	//����д�����.
	AtomicLong writeCount = new AtomicLong(0);	
	/**
	 * �������
	 */
	AtomicLong removeCount = new AtomicLong(0);
	
	//�ж�ͳ�������������.
	final long MAX = Long.MAX_VALUE - 1000000;
	
			
	//����������
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
			throw new RuntimeException("��֧�ֵĴ洢���ͣ�" + this.cacheConfig.getStoreType() + " of " + this.cacheConfig.getCacheName());
		}
	}
	/**
	 ���ͳ������.
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
		// ��ͳ���������
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
			log.warn("Store ��ȡ�洢�����쳣: " + key);
			return null;
		}
	
		if(storeObject == null){
			failCount.incrementAndGet();
			return null;
		}else{
			this.succesCount.incrementAndGet();
		}
		
		V value = null;	
		//��Чʱ���ж�
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
			log.warn("Store Put�洢�����쳣: " + key);
		}	
	}
	public void remove(K key){
		try {
			log.debug("�����������: cacheName=" + this.cacheConfig.getCacheName() + ",key=" + key );
			store.remove(key);
			this.removeCount.incrementAndGet();
		} catch (Exception e) {
			log.warn("Store Remove�洢�����쳣: " + key);
		}

	}
	public String clear() {
		String result = null;
		try {
			store.clear();
			if(log.isInfoEnabled()){
				log.info("���������ɹ�: cacheName=" + this.cacheConfig.getCacheName());
			}
			result = "�������ɹ� " + this.cacheConfig.getCacheName() ;
		} catch (Exception e) {
			log.warn("Store clear�洢�����쳣: " + e.getMessage());
			result = "�������ʧ�� " + this.cacheConfig.getCacheName() ;
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
		return "�������ʱ��ɹ�";
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
