package com.taobao.pamirs.cache.util.asynlog;

import org.junit.Test;

import com.taobao.pamirs.cache.util.asynlog.AsynWriter;

/**
 * �첽log��Ԫ����
 * 
 * @author xiaocheng 2012-11-19
 */
public class AsynWriterTest {

	@Test
	public void testAsynWriter() throws InterruptedException {
		AsynWriter<String> s = new AsynWriter<String>();

		for (int i = 0; i < 8; i++) {
			s.write("abc" + i);
		}

		Thread.sleep(3000);

		// û��assert������־�Ƿ�������
	}

}
