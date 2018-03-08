package com.taobao.pamirs.cache.util.convert;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.commons.lang.StringUtils;

/**
 * 处理下配置文件，替换成mock的bean.
 * 
 * @author <a href="mailto:wancun.chenwc@alibaba-inc.com">qiudao</a>
 * @version 1.0
 * @since 2014年8月15日
 */
public class FileHandler {

	private static final String READ_CHARSET = "gbk";
	private static final String WRITE_CHARSET = "gbk";

	public static void main(String[] args) throws Exception {
		File file = covertFile("D:/ws/ws_tmall_trunk/taobao-pamirs-proxycache/src/test/resources/convert/biz-cache.xml");
		System.out.println(file.getAbsolutePath());
	}

	public static File covertFile(String file) throws Exception {
		File f = new File(file);
		File f2 = new File(file + "result.xml");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), READ_CHARSET));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f2), WRITE_CHARSET));
		bw.write("<?xml version=\"1.0\" encoding=\"" + WRITE_CHARSET + "\"?>\r\n");
		bw.write("<!DOCTYPE beans PUBLIC \"-//SPRING//DTD BEAN//EN\" \"http://www.springframework.org/dtd/spring-beans.dtd\">\r\n");
		bw.write("<beans default-autowire=\"byName\">\r\n");
		String line = null;

		boolean beanStart = false;
		boolean beanEnd = false;
		while ((line = br.readLine()) != null) {
			if (StringUtils.isEmpty(line))
				continue;

			if (!beanStart) {
				beanStart = line.indexOf("<bean ") >= 0 && line.indexOf("CacheManager") > 0;
				if (beanStart) {
					bw.write("<bean id=\"cacheManager\" class=\"com.taobao.pamirs.cache.util.convert.MockCacheManager\" >"
							+ "\r\n");
					continue;
				}
			}
			if (!beanEnd && beanStart) {
				beanEnd = line.indexOf("</bean>") >= 0;
				if (beanEnd) {
					bw.write(line + "\r\n");
				}
			}
			if (beanStart && !beanEnd) {
				bw.write(line + "\r\n");
			}
		}
		bw.write("</beans>");
		bw.flush();
		bw.close();
		br.close();
		return f2;
	}
}
