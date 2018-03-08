package com.taobao.pamirs.cache.store.tair;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.pamirs.cache.store.Store;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;
import com.taobao.tair.TairManager;

/**
 * TairStore �����Ա� Tair ��ͳһ����洢����.
 * ͨ�� Key-Value ����ʽ�����л���Ķ������ Tair ��������.
 * 
 * ֻ�ܲ���  PUT , GET , REMOVE ������ Key ����.
 * ����ʹ�� CLEAR , CLEAN ���ַ�Χ�������.
 * 
 * ʹ�ø� Store . ���������ԱȽϴ�.  5G ~ 10G
 * ���Ǳ����ǹ̶�����. ���ݲ��ᷢ���仯.
 * 
 * ������ʷ��������.
 * 
 * ������������Ϣ. ���� ��Ʒ������Ϣ. 
 * ������ø� Store.
 * ��Ҫ�����жϻ��������Ƿ�ʱ. �����ʱ. �򷵻� Null
 * �൱�ڻ���û������. 
 * 
 * Ȼ���ⲿ Cache ������ݿ��ȡ����. Ȼ������ Store .
 * �������ϵĹ�������.
 * 
 * 
 * **/
public class TairCacheStore<K,V> implements Store<K,V>{
	
	private static final Log logger = LogFactory.getLog(TairCacheStore.class);
	
	/* ��������� */
	private TairManager tairManager;
	private String cacheCode;

	/* �洢���� */
	private String region;
	private int namespace;	
    
	/* ����ʱ�� 1�� */
	private int expire = 0;
	
	public TairCacheStore(String cacheCode, String region, int namespace){
		this.cacheCode = cacheCode;
		this.region = region;
		this.namespace = namespace;
		this.tairManager = TairStoreFactory.getTairManager();
	}
    
	private String getTairKey(Object key){
		StringBuilder cacheKey = new StringBuilder();
		cacheKey.append(region)
				.append(cacheCode)
				.append(key);
		return cacheKey.toString();
	}
		
	@SuppressWarnings("unchecked")
	public V get(K key) {
		
		Object returnValue = null;
		Result<DataEntry> result = tairManager.get(namespace, getTairKey(key));
		if (result.isSuccess()) {
			DataEntry data = result.getValue();// ��һ��getValue����DataEntry����												
			if (data != null){
				returnValue = data.getValue();// �ڶ���getValue����������value
			}
		}
		if (returnValue == null)
			logger.warn("Tair Cache failed to get object [namespace="
					+ namespace + ", key=" + getTairKey(key)
					+ "]. Error Message : " + result.getRc().getMessage());
		return (V) returnValue;
	}
	
	 
	@SuppressWarnings("deprecation")
	public void remove(K key) {
		// �������ʧЧ ����Ϊ �շ���ϵͳ�� �����ڲ�ͬ�ļ�Ⱥ�У����֣�
		ResultCode rc = tairManager.invalid(namespace, getTairKey(key));
		if (!ResultCode.SUCCESS.equals(rc)) {
			logger.error("Tair Cache failed to invalid object [namespace="
					+ namespace + ", key=" + getTairKey(key)
					+ "]. Error Message : " + rc.getMessage());
		}
	}
	public void put(K key, V value) {
		//putǰ��Ҫremove
		remove(key);
		ResultCode rc = tairManager.put(namespace, getTairKey(key),
				(Serializable) value, 0, expire);
		
		if (!ResultCode.SUCCESS.equals(rc)) {
			logger.error("Tair Cache failed to put object [namespace="
					+ namespace + ", key=" + getTairKey(key) + ", value="
					+ value + "]. Error Message : " + rc.getMessage());
		}
	}
	
	public void replaceData(Map<K, V> newData) {
		throw new RuntimeException("Tair�洢 ��֧�ִ˷���");
	}

	public void clear() {
		throw new RuntimeException("Tair�洢 ��֧�ִ˷���");
	}

	public int size() {		
		throw new RuntimeException("Tair�洢 ��֧�ִ˷���");
	}
}
