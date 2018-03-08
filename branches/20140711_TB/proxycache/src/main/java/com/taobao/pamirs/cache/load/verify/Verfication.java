package com.taobao.pamirs.cache.load.verify;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.taobao.pamirs.cache.store.StoreType;

/**
 * У��ע��
 * 
 * @author xiaocheng 2012-11-29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Verfication {

	/**
	 * У���ֶ�����,���ڴ�����ʾ
	 * 
	 * @return
	 */
	String name();

	/**
	 * �ж��Ƿ����Ϊnull
	 * 
	 * @return
	 */
	boolean notNull() default false;

	/**
	 * �ж�String�Ƿ����Ϊnull����ַ��� <br>
	 * ��only for String Type��
	 * 
	 * @return
	 */
	boolean notEmpty() default false;

	/**
	 * ������ʽ����<br>
	 * �÷���{"regx", "��ʾ��Ϣ"} <br>
	 * ��only for String Type��
	 * 
	 * @return
	 */
	String[] regx() default {};

	/**
	 * �ַ�����󳤶ȣ������������ַ���ȫ��Ӣ���������ַ� <br>
	 * ��only for String��
	 * 
	 * @return
	 */
	int maxlength() default 0;

	/**
	 * �ַ�����С���ȣ������������ַ���ȫ��Ӣ���������ַ� <br>
	 * ��only for String��
	 * 
	 * @return
	 */
	int minlength() default 0;

	/**
	 * �ж�List�Ƿ�Ϊ��
	 * 
	 * @return
	 */
	boolean notEmptyList() default false;

	/* ------ ҵ����� ------- */

	/**
	 * �ж��Ƿ�������StoreType
	 * 
	 * @return
	 */
	boolean isStoreType() default false;

	/**
	 * ��Typeʱ�Ż�У�飬Ĭ�϶�У��
	 * 
	 * @return
	 */
	StoreType[] when() default {};

}
