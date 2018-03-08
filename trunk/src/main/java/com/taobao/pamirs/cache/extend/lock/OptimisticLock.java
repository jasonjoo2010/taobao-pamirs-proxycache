package com.taobao.pamirs.cache.extend.lock;

import com.taobao.pamirs.cache.extend.lock.util.LockException;

/**
 * �ֲ�ʽ�ֹ���
 * 
 * <pre>
 * try {
 * 	long lockVersion = optimisticLock.getLockVersion(1L, &quot;abc&quot;);
 * 	// do something
 * 	optimisticLock.freeLock(1L, &quot;abc&quot;, lockVersion);
 * } catch (LockException e) {
 * 	// ��ʧ�ܴ���
 * }
 * </pre>
 * 
 * @author xiaocheng Sep 29, 2015
 */
public interface OptimisticLock {

	/**
	 * ��ȡ�ֲ�ʽ���汾
	 * 
	 * @param objType
	 * @param objId
	 * @return ��ǰ���汾��
	 * @throws LockException
	 */
	int getLockVersion(long objType, String objId) throws LockException;

	/**
	 * �ͷ���Դʵ���������version�Ѿ��仯���ͷ�ʧ�����쳣
	 * 
	 * @param objType
	 * @param objId
	 * @param lockVersion
	 * @throws LockException
	 */
	void freeLock(long objType, String objId, int lockVersion)
			throws LockException;

}
