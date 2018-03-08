package com.taobao.pamirs.cache.framework.listener;

import static com.taobao.pamirs.cache.framework.listener.CacheOprator.GET;
import static com.taobao.pamirs.cache.framework.listener.CacheOprator.PUT;
import static com.taobao.pamirs.cache.framework.listener.CacheOprator.PUT_EXPIRE;
import static com.taobao.pamirs.cache.framework.listener.CacheOprator.REMOVE;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Ĭ�ϵĻ��湫��ͳ����Ϣlistener
 * 
 * @author xiaocheng 2012-11-8
 */
public class DefaultStatisticInfoListener implements CacheOprateListener {

	/**
	 * ͳ�����ݵ�ʱ�䴰�ڣ���λ������
	 */
	private long timeWindow = 24 * 60 * 60 * 1000L;// Ĭ��1��

	/**
	 * ���һ�������ʱ��
	 */
	private long lastClearTime = System.currentTimeMillis();

	/**
	 * GET�������д���
	 */
	private AtomicLong readSuccessCount = new AtomicLong(0);
	/**
	 * GET����δ���д���
	 */
	private AtomicLong readFailCount = new AtomicLong(0);
	/**
	 * GET��������ʱ
	 */
	private AtomicLong readTotalTime = new AtomicLong(0);

	/**
	 * PUT��������
	 */
	private AtomicLong writeCount = new AtomicLong(0);
	/**
	 * PUT��������ʱ
	 */
	private AtomicLong writeTotalTime = new AtomicLong(0);

	/**
	 * REMOVE�����ܴ���
	 */
	private AtomicLong removeCount = new AtomicLong(0);

	@Override
	public void oprate(CacheOprator oprator, CacheOprateInfo cacheInfo) {
		if (oprator == null || cacheInfo == null)
			return;

		// ˢ��ͳ��
		if (System.currentTimeMillis() > (lastClearTime + timeWindow)) {
			clear();
		}

		if (oprator == GET) {
			if (cacheInfo.isHitting()) {
				readSuccessCount.incrementAndGet();
			} else {
				readFailCount.incrementAndGet();
			}

			readTotalTime.addAndGet(cacheInfo.getMethodTime());
		} else if (oprator == PUT || oprator == PUT_EXPIRE) {
			writeCount.incrementAndGet();
			writeTotalTime.addAndGet(cacheInfo.getMethodTime());
		} else if (oprator == REMOVE) {
			removeCount.incrementAndGet();
		}

	}

	/**
	 * �����������
	 * 
	 * @return
	 */
	public String getReadHitRate() {
		long successCount = readSuccessCount.get();
		long failCount = readFailCount.get();
		long allCount = successCount + failCount;

		if (allCount == 0)
			return "%";

		BigDecimal hitRated = new BigDecimal(successCount * 100 / allCount);
		return hitRated.toString() + "%";
	}

	/**
	 * �����ƽ��ʱ��
	 * 
	 * @return
	 */
	public long getReadAvgTime() {
		long successCount = readSuccessCount.get();
		long failCount = readFailCount.get();
		long allCount = successCount + failCount;
		long totalTime = readTotalTime.get();

		if (allCount == 0)
			return 0L;

		return totalTime / allCount;
	}

	/**
	 * ����дƽ��ʱ��
	 * 
	 * @return
	 */
	public long getWriteAvgTime() {

		long count = writeCount.get();
		long totalTime = writeTotalTime.get();

		if (count == 0)
			return 0L;

		return totalTime / count;
	}

	/**
	 * ���ͳ������.
	 */
	private void clear() {
		readSuccessCount.set(0);
		readFailCount.set(0);
		readTotalTime.set(0);
		writeCount.set(0);
		writeCount.set(0);
		removeCount.set(0);

		lastClearTime = System.currentTimeMillis();
	}

	public AtomicLong getReadSuccessCount() {
		return readSuccessCount;
	}

	public void setReadSuccessCount(AtomicLong readSuccessCount) {
		this.readSuccessCount = readSuccessCount;
	}

	public AtomicLong getReadFailCount() {
		return readFailCount;
	}

	public void setReadFailCount(AtomicLong readFailCount) {
		this.readFailCount = readFailCount;
	}

	public AtomicLong getReadTotalTime() {
		return readTotalTime;
	}

	public void setReadTotalTime(AtomicLong readTotalTime) {
		this.readTotalTime = readTotalTime;
	}

	public AtomicLong getWriteCount() {
		return writeCount;
	}

	public void setWriteCount(AtomicLong writeCount) {
		this.writeCount = writeCount;
	}

	public AtomicLong getWriteTotalTime() {
		return writeTotalTime;
	}

	public void setWriteTotalTime(AtomicLong writeTotalTime) {
		this.writeTotalTime = writeTotalTime;
	}

	public AtomicLong getRemoveCount() {
		return removeCount;
	}

	public void setRemoveCount(AtomicLong removeCount) {
		this.removeCount = removeCount;
	}

	public long getTimeWindow() {
		return timeWindow;
	}

	public void setTimeWindow(long timeWindow) {
		this.timeWindow = timeWindow;
	}

	public long getLastClearTime() {
		return lastClearTime;
	}

	public void setLastClearTime(long lastClearTime) {
		this.lastClearTime = lastClearTime;
	}

}
