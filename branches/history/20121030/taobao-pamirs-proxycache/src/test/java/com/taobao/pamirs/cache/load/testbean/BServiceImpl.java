package com.taobao.pamirs.cache.load.testbean;

import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * ����Bean-B������֧�ֵĸ��ֲ�����
 * 
 * @author xiaocheng 2012-11-19
 */
@Component("bService")
public class BServiceImpl implements BService {

	@Override
	public String doVarietyArgs(boolean a, Boolean b, char c, Character d,
			byte e, Byte f, short g, Short h, int j, Integer k, long l, Long m,
			float n, Float o, double p, Double q, Date r, String s) {

		return null;
	}

}
