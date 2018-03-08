package com.taobao.pamirs.cache.store.tair;

import java.io.Serializable;

import com.taobao.pamirs.cache.framework.CacheException;
import com.taobao.pamirs.cache.framework.ICache;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;
import com.taobao.tair.TairManager;

/**
 * TairStore 采用淘宝 Tair 的统一缓存存储方案.
 * <p>
 * 
 * <pre>
 * 通过 Key-Value 的形式将序列化后的对象存入 Tair 服务器中.
 * 
 * 只能采用 PUT , PUT_EXPIRETIME , GET , REMOVE 这四种 Key 操作. 
 * 不能使用 CLEAR , CLEAN 这种范围清除操作.
 * 
 * 使用该 Store . 数据量可以比较大. 5G ~ 10G 
 * 适用于数据量大但变化较少的场合.
 * 
 * 例如：商品数据、订单数据.
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
			DataEntry tairData = result.getValue();// 第一个getValue返回DataEntry对象
			if (tairData == null)
				return null;

			try {
				return (V) tairData.getValue();// 第二个getValue返回真正的value
			} catch (ClassCastException e) {
				// 转换出错，主要用于兼容老的包装
				this.remove(key);
				throw new CacheException(1024,
						"TairStore-ClassCastException(缓存对象不兼容性错误)");
			}
		}

		// 失败（包括已经expireTime到期）
		throw new CacheException(result.getRc().getCode(), result.getRc()
				.getMessage());
	}

	@Override
	public void put(K key, V value) {
		// put前需要remove
		remove(key);

		ResultCode rc = tairManager.put(namespace, key, value);

		// 失败
		if (!rc.isSuccess()) {
			throw new CacheException(rc.getCode(), rc.getMessage());
		}
	}

	@Override
	public void put(K key, V value, int expireTime) {
		// put前需要remove
		remove(key);

		ResultCode rc = tairManager.put(namespace, key, value, 0, expireTime);

		// 失败
		if (!rc.isSuccess()) {
			throw new CacheException(rc.getCode(), rc.getMessage());
		}
	}

	@Override
	public void remove(K key) {
		// TODO xiaocheng
		// 这里采用失效 是因为 收费线系统将 会有在不同的集群中（容灾）

		ResultCode rc = tairManager.invalid(namespace, key);

		// 失败
		if (!rc.isSuccess()) {
			throw new CacheException(rc.getCode(), rc.getMessage());
		}
	}

	@Override
	public void clear() {
		throw new RuntimeException("Tair存储 不支持此方法");
	}

	@Override
	public int size() {
		throw new RuntimeException("Tair存储 不支持此方法");
	}

}
