package com.taobao.pamirs.cache.framework.timer;

import java.io.Serializable;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.pamirs.cache.framework.CacheProxy;

/**
 * ��������Timer����
 * 
 * @author xiaocheng 2012-11-8
 */
public class CleanCacheTimerManager {

	private static final Log log = LogFactory.getLog(CleanCacheTimerManager.class);

	private Timer timer;

	public CleanCacheTimerManager() {
		timer = new Timer("CleanCacheTimerManager", false);// �ػ�����
	}

	public void createCleanCacheTask(
			final CacheProxy<Serializable, Serializable> cache,
			final String aCronTabExpress) throws Exception {

		CronExpression cexp = new CronExpression(aCronTabExpress);
		Date nextTime = cexp.getNextValidTimeAfter(new Date(System
				.currentTimeMillis()));

		CleanCacheTask task = new CleanCacheTask(cache, new CleanNotice() {

			@Override
			public void cleaned(CleanCacheTask task) {
				// 1. ȷ����������
				task.cancel();

				// 2. ����һ���µģ���ΪҪ֧���Զ���ĵ��ȱ��ʽCronExpression��
				try {
					CleanCacheTimerManager.this.createCleanCacheTask(cache,
							aCronTabExpress);
				} catch (Exception e) {
					log.fatal("���ش��󣬶�ʱ������ʧ�ܣ�" + e.getMessage(), e);
				}

			}
		});

		this.timer.schedule(task, nextTime);
	}

	/**
	 * ������ɺ�֪ͨ
	 * 
	 * @author xiaocheng 2012-11-8
	 */
	interface CleanNotice {
		void cleaned(CleanCacheTask task);
	}

	/**
	 * ����Task
	 * 
	 * @author xiaocheng 2012-11-8
	 */
	class CleanCacheTask extends TimerTask {

		private CacheProxy<Serializable, Serializable> cache;

		/** �����֪ͨ */
		private CleanNotice notice;

		public CleanCacheTask(CacheProxy<Serializable, Serializable> cache,
				CleanNotice notice) {
			this.cache = cache;
			this.notice = notice;
		}

		@Override
		public void run() {
			try {
				cache.clear();
			} catch (Exception e) {
				log.error("����Mapʧ��", e);
			} finally {
				notice.cleaned(this);
			}
		}

	}
}
