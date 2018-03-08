package com.taobao.pamirs.cache.extend.lock;

/**
 * �ֲ�ʽ����������֧�֡����롯��
 * 
 * <pre>
 * boolean lockSuccess = pessimisticLock.lock(1L, &quot;abc&quot;);
 * if (lockSuccess) {
 * 	try {
 * 		// do something
 * 	} finally {
 * 		pessimisticLock.unlock(1L, &quot;abc&quot;);
 * 	}
 * }
 * </pre>
 * 
 * @author xiaocheng Aug 18, 2015
 */
public interface PessimisticLock {

	public static final int DEFAULT_EXPIRE_SECONDS = 3;// Ĭ��������ʱ�䣬��λ����

	/**
	 * ���Ի�ȡ�ֲ�ʽ��������Ĭ��������ʱ�䣩
	 * 
	 * @param objType
	 * @param objId
	 */
	boolean lock(long objType, String objId);

	/**
	 * ���Ի�ȡ�ֲ�ʽ��
	 * 
	 * @param objType
	 * @param objId
	 * @param expireSeconds
	 *            ָ��������ʱ��
	 */
	boolean lock(long objType, String objId, int expireSeconds);

	/**
	 * �ͷ���
	 * 
	 * @param objType
	 * @param objId
	 * @return ���ͷ�ʧ�ܰ�װ�쳣������false���������Ի�ȴ�����ʱ
	 */
	boolean unlock(long objType, String objId);

}
