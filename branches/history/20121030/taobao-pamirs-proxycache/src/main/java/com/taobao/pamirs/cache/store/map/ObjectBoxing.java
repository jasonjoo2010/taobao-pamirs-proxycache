package com.taobao.pamirs.cache.store.map;

import java.io.Serializable;

/**
 * ��������װ�֧࣬��expireTime
 * 
 * @author xuanyu
 * @author xiaocheng 2012-10-30
 */
public class ObjectBoxing<V extends Serializable> implements Serializable {
	//
	private static final long serialVersionUID = 2186360043715004471L;

	private Long timestamp = new Long(System.currentTimeMillis() / 1000);

	/**
	 * ʧЧʱ�䣨����ʱ�䣩����λ����<br>
	 * Null��ʾ����ʧЧ
	 */
	private Integer expireTime;

	private V value;

	public ObjectBoxing(V value) {
		this(value, null);
	}

	public ObjectBoxing(V value, Integer expireTime) {
		this.value = value;
		this.expireTime = expireTime;
	}

	public V getObject() {
		// �Ѿ�ʧЧ
		if (expireTime != null && expireTime != 0) {
			long now = System.currentTimeMillis() / 1000;

			if (timestamp.longValue() > expireTime.longValue()) {// ���ʱ��
				if (now >= (expireTime.longValue() + timestamp.longValue()))
					return null;
			} else {// ����ʱ��
				if (now >= expireTime.longValue())
					return null;
			}
		}

		return this.value;
	}
}