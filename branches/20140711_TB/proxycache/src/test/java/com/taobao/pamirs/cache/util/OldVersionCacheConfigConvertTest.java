package com.taobao.pamirs.cache.util;

import com.taobao.pamirs.cache.util.convert.OldVersionCacheConfigConvert;

/**
 * TODO ��˵��
 * 
 * @author qiudao
 * @version 1.0
 * @since 2014��10��31��
 */
public class OldVersionCacheConfigConvertTest {

	public static void main(String[] args) throws Exception {
		String path = "D:/�ճ�����/cache����/cachemanage/src/test/resources/convert/biz-cache.xml";
		OldVersionCacheConfigConvert.convertToNewFile(path);
	}
}
