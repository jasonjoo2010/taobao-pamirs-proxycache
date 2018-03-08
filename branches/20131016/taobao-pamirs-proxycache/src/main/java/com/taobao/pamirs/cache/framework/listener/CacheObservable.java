package com.taobao.pamirs.cache.framework.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 缓存观察者
 * 
 * @author xiaocheng 2012-10-31
 */
public class CacheObservable {

	private Lock lock = new ReentrantLock(true);

	private List<CacheOprateListener> listeners = new ArrayList<CacheOprateListener>();

	public void addListener(CacheOprateListener listener) {
		if (listener != null && listeners.size() > 0) {
			lock.lock();
			try {
				listeners.add(listener);
			} finally {
				lock.unlock();
			}
		}
	}
	/**
	 * 有监听者
	 * @return
	 */
	public boolean havListeners() {
		if (listeners != null && listeners.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public void deleteListener(CacheOprateListener listener) {
		if (listener != null && listeners.size() > 0) {
			lock.lock();
			try {
				listeners.remove(listener);
			} finally {
				lock.unlock();
			}
		}
	}

	public void notifyListeners(CacheOprator oprator, CacheOprateInfo cacheInfo) {
		if (listeners != null && listeners.size() > 0) {
			lock.lock();
			try {
				for (CacheOprateListener obs : listeners) {
					obs.oprate(oprator, cacheInfo);
				}
			} finally {
				lock.unlock();
			}
		}
	}

}
