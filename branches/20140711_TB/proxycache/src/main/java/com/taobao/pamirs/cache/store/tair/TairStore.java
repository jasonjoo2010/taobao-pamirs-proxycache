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
	public void put(K key, V value,int version,int expireTime) {
		
		ResultCode rc = tairManager.put(namespace, key, value, version, expireTime);
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
		if (!rc.isSuccess()&&!ResultCode.DATANOTEXSITS.equals(rc.getCode())) {
			ResultCode rc2 = tairManager.invalid(namespace, key);
			if (!rc2.isSuccess()&&!ResultCode.DATANOTEXSITS.equals(rc2.getCode())) {
				throw new CacheException(rc2.getCode(), rc2.getMessage());
			}
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

	@Override
	@Deprecated
	public void hidden(K key) {
		ResultCode rc =tairManager.hideByProxy(namespace, key);
		// 失败
		if (!rc.isSuccess()) {
			ResultCode rc2 = tairManager.hideByProxy(namespace, key);
			if (!rc2.isSuccess()) {
				throw new CacheException(rc2.getCode(), rc2.getMessage());
			}
		}
	}

	
	/**
	 * 获取版本号，用于解决并发场景多个put、remove到达顺序不确定导致的缓存不一致问题
	 * 1、在cacheManager下使用缓存，只有当线程get为null时才会触发put操作，
	 * 	     该key前一个操作为invalid或者hidden
	 * 2、通过携带版本号put缓存时，当多个线程  同时put时（并发时，如果强制覆盖可能出现不一致而不知晓），只能有一个线程成功，
	 *    其他它线程失败
	 * 3、当出现版本号错误时，此时缓存可能已经不一致，此时通过日志监控人工解决（小概率事件）。不能线程清除失败后invalid，
	 *    因为清除操作可能导致其他并发线程把不一致数据写进去了，sleep一个值在再invalid的方式也不是很好，
	 *    cacheManager自己维护一个异步队列的方式代价又太大。当业务需要力保一致性时，可以自行通过写集中控制管理的架构来进行了，此时cacheManager已经不适用了
	 * 4、该key前一个操作invalid时，返回一个版本常量即可，当前一个操作未hidden时，需要获取隐藏数据的版本号
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
