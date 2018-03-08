package com.taobao.pamirs.cache.load.verify;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.util.CollectionUtils;

import com.taobao.pamirs.cache.store.StoreType;

/**
 * ����Annotation��̬У�飬�����ظ�����
 * 
 * @author xiaocheng 2012-11-29
 */
public class StaticCheck {
	


	/**
	 * У�����FIELD�е�Verfication
	 * 
	 * @param o
	 * @throws Exception
	 *             ��ʧ�ܻ����쳣
	 */
	@SuppressWarnings("rawtypes")
	public static void check(Object o) throws Exception {

		// 1. fields have annotation
		Map<String, Field> map = new HashMap<String, Field>();
		Class<?> superclass = o.getClass();
		do {
			Field[] fields = superclass.getDeclaredFields();
			for (Field f : fields) {
				Verfication annotation = f.getAnnotation(Verfication.class);
				if (annotation != null)
					map.put(f.getName(), f);
			}

			superclass = superclass.getSuperclass();
		} while (superclass != null && superclass != Object.class);

		// 2. do validate
		PropertyDescriptor[] pds = java.beans.Introspector.getBeanInfo(
				o.getClass()).getPropertyDescriptors();
		for (PropertyDescriptor pd : pds) {
			Field f = map.get(pd.getName());
			if (f == null)
				continue;

			// must have get() method
			Object fieldValue = pd.getReadMethod().invoke(o);

			Verfication v = f.getAnnotation(Verfication.class);

			StoreType[] vWhen = v.when();
			String vName = v.name();
			boolean vNotNull = v.notNull();
			boolean vNotEmpty = v.notEmpty();
			int vMaxlength = v.maxlength();
			int vMinlength = v.minlength();
			String[] vRegx = v.regx();
			boolean vNotEmptyList = v.notEmptyList();
			boolean vIsStoreType = v.isStoreType();

			// typeName\when
			if (vWhen.length > 0) {
				Method m = o.getClass().getMethod("getStoreType");
				if (m != null) {
					String storeType = (String) m.invoke(o);

					// ����vWhenTypes�ڣ�����֤
					if (!isIn(storeType, vWhen))
						continue;
				}
			}

			if (vNotNull && fieldValue == null)
				throw new Exception(vName + "����Ϊ�գ�");

			if (f.getType().equals(String.class)) {
				if (vNotEmpty) {
					if (fieldValue == null
							|| "".equals(((String) fieldValue).trim()))
						throw new Exception(vName + "����Ϊ�գ�");
				}

				if (vMaxlength > 0) {
					if (getStringActualLength((String) fieldValue) > vMaxlength)
						throw new Exception(vName + "���ܳ���" + vMaxlength
								+ "���ַ�(һ����Ŀ�������ַ�)��");
				}

				if (vMinlength > 0) {
					if (getStringActualLength((String) fieldValue) < vMinlength)
						throw new Exception(vName + "����С��" + vMinlength
								+ "���ַ���");
				}

				if (vRegx != null && vRegx.length == 2) {
					if (!Pattern.compile(vRegx[0]).matcher((String) fieldValue)
							.matches()) {
						throw new Exception(vName + vRegx[1] + "��");
					}
				}
			}

			if (List.class.isAssignableFrom(f.getType())) {
				if (vNotEmptyList && CollectionUtils.isEmpty((List) fieldValue)) {
					throw new Exception(vName + "����Ϊ��List��");
				}
			}
			
			if (vIsStoreType) {
				if (StoreType.toEnum((String) fieldValue) == null)
					throw new Exception(vName + "=" + fieldValue
							+ ",���ǺϷ���StoreType��");
			}

		}
	}

	private static boolean isIn(String storeType, StoreType[] types) {
		for (StoreType type : types) {
			if (StoreType.toEnum(storeType) == type)
				return true;
		}

		return false;
	}

	/**
	 * �����ַ������ȣ������������ַ���ȫ��Ӣ���������ַ�
	 * 
	 * @param str
	 *            Ҫ������ַ�����������Ϊ��
	 * @return
	 */
	private static int getStringActualLength(String str) {
		if (isBlank(str))
			return 0;

		int length = 0;
		for (int i = 0; i < str.length(); i++) {
			if (isChineseChar(str.charAt(i))) {
				length += 2;
			} else {
				if (isFullSpaceChar(str.charAt(i))) {
					length += 2;
				} else {
					length++;
				}
			}
		}
		return length;
	}

	/**
	 * �Д��ַ����Ƿ��հ� str == null true str == "" true str == "    " true
	 * 
	 * @param str
	 * @return
	 */
	private static boolean isBlank(String str) {
		if (str == null) {
			return true;
		}

		if (str.trim().length() == 0) {
			return true;
		}

		return false;
	}

	/**
	 * �ж��Ƿ�Ϊȫ���ַ�
	 * 
	 * @param ch
	 * @return
	 */
	private static boolean isFullSpaceChar(char ch) {
		return (ch >= 0xff00 && ch <= 0xffff);
	}

	/**
	 * �ж��ַ��ǲ��������ַ�
	 * 
	 * @param str
	 *            �ַ�
	 * @return true �������ַ���false ���������ַ�
	 */
	private static boolean isChineseChar(char str) {
		return (str >= 0x4e00 && str <= 0x9fbb);
	}

}
