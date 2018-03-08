package com.taobao.pamirs.cache.store.tair;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.taobao.pamirs.cache.framework.CacheException;
import com.taobao.tair.TairManager;

/**
 * tair store��Ԫ����
 * 
 * @author xiaocheng 2012-11-19
 */
@SpringApplicationContext({ "/store/tair-store.xml" })
public class TairStoreTest extends UnitilsJUnit4 {

	@SpringBeanByName
	TairManager tairManager;
	TairStore<String, String> store;

	@Before
	public void init() {
		if (store == null)
			store = new TairStore<String, String>(tairManager, 318);
	}

	@Test
	public void testPutAndGet() {
		String key = "123";
		String value = "jeck";

		assertThat(store.get(key), nullValue());

		store.put(key,value,0,0);
		assertThat(store.get(key), is(value));

		// �����������
		store.remove(key);
	}

	@Test
	public void testPutAndExpireTime() throws Exception {
		String key = "999";
		String value = "expire jeck";

		store.put(key, value, 3,0);
		assertThat(store.get(key), is(value));

		Thread.sleep(5000);

		boolean expire = false;
		try {
			store.get(key);
		} catch (CacheException e) {
			expire = true;
		}
		assertThat(expire, is(true));

		// �����������
		store.remove(key);
	}

	@Test
	public void testRemove() {
		String key = "remove";
		String value = "remove jeck";

		store.put(key, value,0,0);
		assertThat(store.get(key), is(value));

		store.remove(key);
		assertThat(store.get(key), nullValue());
	}

	@Test
	public void testClear() {
		try {
			store.clear();
			assertThat("��Ӧ�����е���", true, is(false));
		} catch (Exception e) {
			assertThat(e instanceof RuntimeException, is(true));
		}
	}

	@Test
	public void testSize() {
		try {
			store.size();
			assertThat("��Ӧ�����е���", true, is(false));
		} catch (Exception e) {
			assertThat(e instanceof RuntimeException, is(true));
		}
	}

}
