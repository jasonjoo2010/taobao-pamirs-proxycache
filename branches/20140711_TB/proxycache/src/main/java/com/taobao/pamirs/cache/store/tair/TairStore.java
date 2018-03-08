package com.taobao.pamirs.cache.store.tair;

import java.io.Serializable;

import com.taobao.pamirs.cache.framework.CacheException;
import com.taobao.pamirs.cache.framework.ICache;
import com.taobao.pamirs.cache.store.RemoveMode;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;
import com.taobao.tair.TairManager;

/**
 * TairStore �����Ա� Tair ��ͳһ����洢����.
 * <p>
 * 
 * <pre>
 * ͨ�� Key-Value ����ʽ�����л���Ķ������ Tair ��������.
 * 
 * ֻ�ܲ��� PUT , PUT_EXPIRETIME , GET , REMOVE ������ Key ����. 
 * ����ʹ�� CLEAR , CLEAN ���ַ�Χ�������.
 * 
 * ʹ�ø� Store . ���������ԱȽϴ�. 5G ~ 10G 
 * �������������󵫱仯���ٵĳ���.
 * 
 * ���磺��Ʒ���ݡ���������.
 * </pre>
 * 
 * @author xuanyu
 * @author xiaocheng 2012-11-2
 */
public class TairStore<K extends Serializable, V extends Serializable>
		implements ICache<K, V> {

	private TairManager tairManager;

	private int namespace;

	public TairStore(TairManager tairManager, int namespace) {
		this.tairManager = tairManager;
		this.namespace = namespace;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(K key) {
		Result<DataEntry> result = tairManager.get(namespace, key);
		if (result.isSuccess()) {
			DataEntry tairData = result.getValue();// ��һ��getValue����DataEntry����
			if (tairData == null)
				return null;

			try {
				return (V) tairData.getValue();// �ڶ���getValue����������value
			} catch (ClassCastException e) {
				// ת��������Ҫ���ڼ����ϵİ�װ
				this.remove(key);
				throw new CacheException(1024,
						"TairStore-ClassCastException(������󲻼����Դ���)");
			}
		}

		// ʧ�ܣ������Ѿ�expireTime���ڣ�
		throw new CacheException(result.getRc().getCode(), result.getRc()
				.getMessage());
	}
	
	@Override
	public void put(K key, V value,int version,int expireTime) {
		
		ResultCode rc = tairManager.put(namespace, key, value, version, expireTime);
		// ʧ��
		if (!rc.isSuccess()) {
			throw new CacheException(rc.getCode(), rc.getMessage());
		}
	}

	@Override
	public void remove(K key) {
		// TODO xiaocheng
		// �������ʧЧ ����Ϊ �շ���ϵͳ�� �����ڲ�ͬ�ļ�Ⱥ�У����֣�

		ResultCode rc = tairManager.invalid(namespace, key);

		// ʧ��
		if (!rc.isSuccess()&&!ResultCode.DATANOTEXSITS.equals(rc.getCode())) {
			ResultCode rc2 = tairManager.invalid(namespace, key);
			if (!rc2.isSuccess()&&!ResultCode.DATANOTEXSITS.equals(rc2.getCode())) {
				throw new CacheException(rc2.getCode(), rc2.getMessage());
			}
		}
	}

	@Override
	public void clear() {
		throw new RuntimeException("Tair�洢 ��֧�ִ˷���");
	}

	@Override
	public int size() {
		throw new RuntimeException("Tair�洢 ��֧�ִ˷���");
	}

	@Override
	@Deprecated
	public void hidden(K key) {
		ResultCode rc =tairManager.hideByProxy(namespace, key);
		// ʧ��
		if (!rc.isSuccess()) {
			ResultCode rc2 = tairManager.hideByProxy(namespace, key);
			if (!rc2.isSuccess()) {
				throw new CacheException(rc2.getCode(), rc2.getMessage());
			}
		}
	}

	
	/**
	 * ��ȡ�汾�ţ����ڽ�������������put��remove����˳��ȷ�����µĻ��治һ������
	 * 1����cacheManager��ʹ�û��棬ֻ�е��߳�getΪnullʱ�Żᴥ��put������
	 * 	     ��keyǰһ������Ϊinvalid����hidden
	 * 2��ͨ��Я���汾��put����ʱ��������߳�  ͬʱputʱ������ʱ�����ǿ�Ƹ��ǿ��ܳ��ֲ�һ�¶���֪������ֻ����һ���̳߳ɹ���
	 *    �������߳�ʧ��
	 * 3�������ְ汾�Ŵ���ʱ����ʱ��������Ѿ���һ�£���ʱͨ����־����˹������С�����¼����������߳����ʧ�ܺ�invalid��
	 *    ��Ϊ����������ܵ������������̰߳Ѳ�һ������д��ȥ�ˣ�sleepһ��ֵ����invalid�ķ�ʽҲ���Ǻܺã�
	 *    cacheManager�Լ�ά��һ���첽���еķ�ʽ������̫�󡣵�ҵ����Ҫ����һ����ʱ����������ͨ��д���п��ƹ���ļܹ��������ˣ���ʱcacheManager�Ѿ���������
	 * 4����keyǰһ������invalidʱ������һ���汾�������ɣ���ǰһ������δhiddenʱ����Ҫ��ȡ�������ݵİ汾��
	 * @param key
	 * @param removeMode
	 * @return
	 */
	@Override
	@Deprecated
	public Integer getDataVersion(K key, String removeMode) {
		if(RemoveMode.HIDDEN.getName().equals(removeMode)){
			Result<DataEntry> resultH = tairManager.getHidden(namespace, key);
			if (!resultH.isSuccess()) {
				throw new CacheException(resultH.getRc().getCode(), resultH.getRc().getMessage());
			}
			DataEntry tairDataH = resultH.getValue();
			if(tairDataH!=null){
				return tairDataH.getVersion();
			}
		}
		return (int)Short.MAX_VALUE;
	}
}
