package com.taobao.pamirs.cache.util.lru;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * �̰߳�ȫ�������ܵ�LRUCacheMap <br>
 * 
 * <pre>
 * 1. ���÷�������(segment)�����������ܡ� -- by Doug Lea ��ʦ
 * 2. Ϊ����ר����Ƶ�SoftReference��װ������Cache����JVM��OOM
 * </pre>
 * 
 * ע��key is not null; value is not null!
 * 
 * @author xiaocheng 2012-11-16
 */
public class ConcurrentLRUCacheMap<K, V> implements Serializable {
	//
	private static final long serialVersionUID = -6742744299745956041L;

	/** Ĭ�ϴ�С */
	public static final int DEFAULT_INITIAL_CAPACITY = 1 << 10;

	/** Ĭ�ϵķ������� */
	public static final int DEFAULT_CONCURRENCY_LEVEL = 1 << 4;

	/** ������� */
	static final int MAXIMUM_CAPACITY = 1 << 30;

	/** ֧��������Ƭ���� */
	static final int MAX_SEGMENTS = 1 << 16; // slightly conservative

	/**
	 * Mask value for indexing into segments. The upper bits of a key's hash
	 * code are used to choose the segment.
	 */
	private final int segmentMask;

	/**
	 * Shift value for indexing within segments.
	 */
	private final int segmentShift;

	private LRUMapLocked<K, SoftReference<V>, V>[] segments;

	/**
	 * Ĭ�Ϲ�������1024/16
	 */
	public ConcurrentLRUCacheMap() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_CONCURRENCY_LEVEL);
	}

	public ConcurrentLRUCacheMap(int size) {
		this(size, DEFAULT_CONCURRENCY_LEVEL);
	}

	/**
	 * �Ƽ����캯�� <br>
	 * ���key hash�����ȵ㵽����segment�У�����LRU������sizeδ��ʱ��Ҳ���ܱ�remove
	 * 
	 * @param size
	 *            �����ܱ�segmentSize����
	 * @param segmentSize
	 *            ����2�ı���
	 */
	@SuppressWarnings("unchecked")
	public ConcurrentLRUCacheMap(int size, int segmentSize) {
		if (size < 0 || segmentSize <= 0)
			throw new IllegalArgumentException();

		if (segmentSize > MAX_SEGMENTS)
			segmentSize = MAX_SEGMENTS;

		// Find power-of-two sizes best matching arguments
		int sshift = 0;
		int ssize = 1;// ������С��2�ı���
		while (ssize < segmentSize) {
			++sshift;
			ssize <<= 1;
		}

		if (ssize != segmentSize)
			throw new IllegalArgumentException("size must be power-of-two!");

		segmentShift = 32 - sshift;
		segmentMask = ssize - 1;
		this.segments = new LRUMapLocked[ssize];

		if (size > MAXIMUM_CAPACITY)
			size = MAXIMUM_CAPACITY;
		int c = size / ssize;
		if (c * ssize != size)
			throw new IllegalArgumentException(
					"size must divide exactly for segmentSize!");
		if (c * ssize < size)
			++c;
		int cap = 1;// ƽ̯��ÿ������Map��size
		while (cap < c)
			cap <<= 1;

		for (int i = 0; i < this.segments.length; ++i)
			this.segments[i] = new LRUMapLocked<K, SoftReference<V>, V>(cap);
	}

	public V get(K key) {
		int hash = hash(key.hashCode());
		return segmentFor(hash).getEntry(key);
	}

	public void put(K key, V value) {
		if (value == null)
			throw new NullPointerException();

		int hash = hash(key.hashCode());
		segmentFor(hash).addEntry(key, value);
	}

	public void remove(K key) {
		int hash = hash(key.hashCode());
		segmentFor(hash).remove(key);
	}

	public synchronized void clear() {
		for (int i = 0; i < this.segments.length; ++i)
			segments[i].clear();
	}

	public int size() {
		final LRUMapLocked<K, SoftReference<V>, V>[] segments = this.segments;
		long sum = 0;
		for (int i = 0; i < segments.length; ++i) {
			sum += segments[i].size();
		}
		if (sum > Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		else
			return (int) sum;
	}

	/**
	 * Applies a supplemental hash function to a given hashCode, which defends
	 * against poor quality hash functions. This is critical because
	 * ConcurrentHashMap uses power-of-two length hash tables, that otherwise
	 * encounter collisions for hashCodes that do not differ in lower or upper
	 * bits.
	 */
	private static int hash(int h) {
		// Spread bits to regularize both segment and index locations,
		// using variant of single-word Wang/Jenkins hash.
		h += (h << 15) ^ 0xffffcd7d;
		h ^= (h >>> 10);
		h += (h << 3);
		h ^= (h >>> 6);
		h += (h << 2) + (h << 14);
		return h ^ (h >>> 16);
	}

	/**
	 * Returns the segment that should be used for key with given hash
	 * 
	 * @param hash
	 *            the hash code for the key
	 * @return the segment
	 */
	private final LRUMapLocked<K, SoftReference<V>, V> segmentFor(int hash) {
		return segments[(hash >>> segmentShift) & segmentMask];
	}

	/**
	 * �����̰߳�ȫ��LRUMap������Lock��ʽ��������û��ConcurrentLRUMap��
	 * 
	 * @author xiaocheng 2012-11-16
	 */
	public static final class LRUMapLocked<KK, TT extends SoftReference<VV>, VV>
			extends LRUMap<KK, TT> {
		//
		private static final long serialVersionUID = -1357125210052412116L;

		/** map lock */
		private final Lock lock = new ReentrantLock();

		public LRUMapLocked() {
			super();
		}

		public LRUMapLocked(int size) {
			super(size);
		}

		/**
		 * �̰߳�ȫ������put
		 * 
		 * @param key
		 * @param entry
		 */
		@SuppressWarnings("unchecked")
		public void addEntry(KK key, VV entry) {
			lock.lock();
			try {
				SoftReference<VV> sr_entry = new SoftReference<VV>(entry);
				super.put(key, (TT) sr_entry);
			} finally {
				lock.unlock();
			}
		}

		/**
		 * �̰߳�ȫ������get
		 * 
		 * @param key
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public VV getEntry(KK key) {
			lock.lock();
			try {
				SoftReference<TT> sr_entry = (SoftReference<TT>) get(key);
				if (sr_entry == null)
					return null;

				if (sr_entry.get() == null) {
					super.remove(key);
					return null;
				}

				return (VV) sr_entry.get();
			} finally {
				lock.unlock();
			}
		}

		/**
		 * �̰߳�ȫ������remove
		 */
		@Override
		public TT remove(Object key) {
			lock.lock();
			try {
				return super.remove(key);
			} finally {
				lock.unlock();
			}
		}

		/**
		 * �̰߳�ȫ������clear
		 */
		@Override
		public void clear() {
			lock.lock();
			try {
				super.clear();
			} finally {
				lock.unlock();
			}
		}

	}

}
