package com.taobao.pamirs.cache.load.testbean;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 测试BeanA（测试重载方法、clear操作）
 * 
 * @author xiaocheng 2012-11-19
 */
@Component("aService")
public class AServiceImpl implements ASerivce {

	Set<String> names = new HashSet<String>();
	List<String> firstHaveValueKeys = new ArrayList<String>();

	public String md5Name(String name) {
		if (name == null)
			return null;

		names.add(name);

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return md.digest(name.getBytes()).toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public String md5Name(String name, String key) {
		return key + md5Name(name);
	}

	@Override
	public String clearNames(String name, String key) {
		names.clear();
		return names.toString();
	}

	@Override
	public String firstHaveValue(String key) {
		Assert.notNull(key);

		if (firstHaveValueKeys.contains(key))
			return null;

		firstHaveValueKeys.add(key);
		return key;
	}

	@Override
	public String noRewirteMethod(String arg) {
		return arg;
	}

}
