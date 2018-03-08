package com.taobao.pamirs.cache.extend.lock.impl;

import static com.taobao.pamirs.cache.extend.lock.util.LockUtil.combineKey;
import static com.taobao.pamirs.cache.extend.lock.util.LockUtil.isTairTimeout;
import static com.taobao.pamirs.cache.extend.lock.util.LockXrayLog.write;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.taobao.pamirs.cache.extend.lock.OptimisticLock;
import com.taobao.pamirs.cache.extend.lock.util.LockException;
import com.taobao.pamirs.cache.extend.timelog.annotation.TimeLog;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;
import com.taobao.tair.TairManager;

/**
 * Tairʵ�ֵķֲ�ʽ��������
 * 
 * @author xiaocheng Aug 17, 2015
 */
@TimeLog
public class TairOptimisticLock implements OptimisticLock {

	private static final Logger log = getLogger(OptimisticLock.class);
	private static final int es = 3;// Ĭ��������ʱ�䣬��λ����

	private TairManager tairManager;
	private Integer namespace;
	private String region;

	private static String VALUE = "pamirs lock";
	private static final String LOCK = "OPTIMISTIC_LOCK";
	private static final String UNLOCK = "OPTIMISTIC_UNLOCK";

	@Override
	public int getLockVersion(long objType, String objId) throws LockException {
		long start = System.currentTimeMillis();
		String key = combineKey(objType, objId, region);
		int lockVersion = 0;
		Result<DataEntry> r = null;
		ResultCode put = null;

		try {
			r = tairManager.get(namespace, key);
			if (isTairTimeout(r.getRc()))// ��ʱ�Զ�����һ��
				r = tairManager.get(namespace, key);

			// Tairû������ʱ������
			if (ResultCode.DATANOTEXSITS.equals(r.getRc())) {
				put = tairManager.put(namespace, key, VALUE, 2, es);
				if (isTairTimeout(put))// ��ʱ�Զ�����һ��
					put = tairManager.put(namespace, key, VALUE, 2, es);

				if (ResultCode.SUCCESS.equals(put))
					lockVersion = 1;// �����ʼ�汾һ����1
			} else if (ResultCode.SUCCESS.equals(r.getRc())
					&& r.getValue() != null) {
				lockVersion = r.getValue().getVersion();
			}
		} catch (Throwable e) {
			log.error("Get Lock Version Fail!", e);
		}

		boolean success = (lockVersion != 0);

		long end = System.currentTimeMillis();
		write(LOCK, end - start, success, objType, objId, es, r, put);

		// ����
		if (success)
			return lockVersion;
		else
			throw new LockException("��ȡ������ʧ��: type=" + objType + ",id=" + objId);
	}

	@Override
	public void freeLock(long objType, String objId, int lockVersion)
			throws LockException {
		if (lockVersion == 0)
			throw new LockException("Tair�������汾�Ų���Ϊ0���ᵼ�����ж��ɹ�!");

		long start = System.currentTimeMillis();
		String key = combineKey(objType, objId, region);
		ResultCode put = null;

		try {
			put = tairManager.put(namespace, key, VALUE, lockVersion, es);

			if (isTairTimeout(put))// ��ʱ�Զ�����һ��
				put = tairManager.put(namespace, key, VALUE, lockVersion, es);

		} catch (Throwable e) {
			log.error("Free Lock Fail!", e);
		}

		boolean success = ResultCode.SUCCESS.equals(put);

		long end = System.currentTimeMillis();
		write(UNLOCK, end - start, success, objType, objId, es, null, put);

		if (!success)
			throw new LockException("�������汾��ʧЧ: type=" + objType + ",id="
					+ objId + ",v=" + lockVersion);
	}

	public void setTairManager(TairManager tairManager) {
		this.tairManager = tairManager;
	}

	public void setNamespace(Integer namespace) {
		this.namespace = namespace;
	}

	public void setRegion(String region) {
		this.region = region;
	}

}
