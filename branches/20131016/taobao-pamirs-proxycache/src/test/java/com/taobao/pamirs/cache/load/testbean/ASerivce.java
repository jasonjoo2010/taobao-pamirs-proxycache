package com.taobao.pamirs.cache.load.testbean;

/**
 * ����BeanA���������ط�����clear������expire������
 * 
 * @author poxiao.gj
 */
public interface ASerivce {

	String md5Name(String name);

	String md5Name(String name, String key);
	
	String clearNames(String name, String key);

	/**
	 * ��һ�η���ֵ���ڶ��ε��þͻ᷵��null��
	 * 
	 * @param key
	 * @return
	 */
	String firstHaveValue(String key);
	
	/**
	 * ��֤û����������ʱ�����ò���Ҫд����
	 * 
	 * @param arg
	 * @return
	 */
	String noRewirteMethod(String arg);
}
