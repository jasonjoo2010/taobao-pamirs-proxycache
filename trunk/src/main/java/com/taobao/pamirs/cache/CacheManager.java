package com.taobao.pamirs.cache;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.InstanceAlreadyExistsException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.taobao.pamirs.cache.extend.jmx.CacheMbean;
import com.taobao.pamirs.cache.extend.jmx.CacheMbeanListener;
import com.taobao.pamirs.cache.extend.jmx.annotation.JmxMethod;
import com.taobao.pamirs.cache.extend.jmx.mbean.MBeanManagerFactory;
import com.taobao.pamirs.cache.extend.log.xray.XrayLogListener;
import com.taobao.pamirs.cache.framework.CacheProxy;
import com.taobao.pamirs.cache.framework.ICache;
import com.taobao.pamirs.cache.framework.config.CacheBean;
import com.taobao.pamirs.cache.framework.config.CacheConfig;
import com.taobao.pamirs.cache.framework.config.MethodConfig;
import com.taobao.pamirs.cache.framework.timer.CleanCacheTimerManager;
import com.taobao.pamirs.cache.load.ICacheConfigService;
import com.taobao.pamirs.cache.load.verify.CacheConfigVerify;
import com.taobao.pamirs.cache.store.StoreType;
import com.taobao.pamirs.cache.store.map.MapStore;
import com.taobao.pamirs.cache.store.tair.TairStore;
import com.taobao.pamirs.cache.util.CacheCodeUtil;
import com.taobao.pamirs.cache.util.ConfigUtil;
import com.taobao.pamirs.cache.util.lru.ConcurrentLRUCacheMap;
import com.taobao.tair.TairManager;

/**
 * �����������
 * 
 * @author xuanyu
 * @author xiaocheng 2012-11-2
 */
public abstract class CacheManager implements ApplicationContextAware,
		ApplicationListener, ICacheConfigService {

	private static final Log log = LogFactory.getLog(CacheManager.class);

	private CacheConfig cacheConfig;

	/**
	 * ÿһ��method��Ӧһ��adapterʵ��
	 */
	private final Map<String, CacheProxy<Serializable, Serializable>> cacheProxys = new ConcurrentHashMap<String, CacheProxy<Serializable, Serializable>>();

	private TairManager tairManager;

	protected ApplicationContext applicationContext;

	private CleanCacheTimerManager timeTask = new CleanCacheTimerManager();

	private boolean useCache = true;

	/** ��ӡ����������־ **/
	private boolean openCacheLog = false;

	/**
	 * ָ�����ػ���ʱLruMap�Ĵ�С��Ĭ����1024
	 * 
	 * @see StoreType.MAP
	 * @see ConcurrentLRUCacheMap
	 */
	private int localMapSize = ConcurrentLRUCacheMap.DEFAULT_INITIAL_CAPACITY;
	/**
	 * ָ�����ػ���ֶεĴ�С��Ĭ����16
	 * 
	 * @see StoreType.MAP
	 * @see ConcurrentLRUCacheMap
	 */
	private int localMapSegmentSize = ConcurrentLRUCacheMap.DEFAULT_CONCURRENCY_LEVEL;;

	public void init() throws Exception {
		// 1. ����/У��config
		cacheConfig = loadConfig();

		// ������������onApplicationEvent����
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		// ����onApplicationEvent�ԭ���ǽ��CacheManagerHandle����ִ�д�����applicationContext.getBean�����������

		if (event instanceof ContextRefreshedEvent) {
			// 2. �Զ����Ĭ�ϵ�����
			autoFillCacheConfig(cacheConfig);

			// 3. �������úϷ���У��
			verifyCacheConfig(cacheConfig);

			// 4. ��ʼ������
			initCache();
		}
	}

	@Override
	public abstract CacheConfig loadConfig();

	@Override
	public void autoFillCacheConfig(CacheConfig cacheConfig) {
		ConfigUtil.autoFillCacheConfig(cacheConfig, applicationContext);
	}

	@Override
	public void verifyCacheConfig(CacheConfig cacheConfig) {
		CacheConfigVerify.checkCacheConfig(cacheConfig, applicationContext);
	}

	/**
	 * ��ʼ������
	 */
	private void initCache() {
		List<CacheBean> cacheBeans = cacheConfig.getCacheBeans();
		if (cacheBeans != null) {
			// ֻ��ע��cacheBean,ĿǰcacheCleanBeans�����������Ӽ�
			for (CacheBean bean : cacheBeans) {

				List<MethodConfig> cacheMethods = bean.getCacheMethods();
				for (MethodConfig method : cacheMethods) {
					initCacheAdapters(cacheConfig.getStoreRegion(),
							bean.getBeanName(), method,
							cacheConfig.getStoreMapCleanTime());
				}
			}
		}
	}

	/**
	 * ��ʼ��Bean/Method��Ӧ�Ļ��棬������ <br>
	 * 1. CacheProxy <br>
	 * 2. ��ʱ��������storeMapCleanTime <br>
	 * 3. ע��JMX <br>
	 * 4. ע��Xray log <br>
	 * 
	 * @param region
	 * @param cacheBean
	 * @param storeMapCleanTime
	 */
	private void initCacheAdapters(String region, String beanName,
			MethodConfig cacheMethod, String storeMapCleanTime) {
		String key = CacheCodeUtil.getCacheAdapterKey(region, beanName,
				cacheMethod);
		StoreType storeType = StoreType.toEnum(cacheConfig.getStoreType());
		ICache<Serializable, Serializable> cache = null;

		if (StoreType.TAIR == storeType) {
			cache = new TairStore<Serializable, Serializable>(tairManager,
					cacheConfig.getStoreTairNameSpace());
		} else if (StoreType.MAP == storeType) {
			cache = new MapStore<Serializable, Serializable>(localMapSize,
					localMapSegmentSize);
		}

		if (cache != null) {
			// 1. CacheProxy
			CacheProxy<Serializable, Serializable> cacheProxy = new CacheProxy<Serializable, Serializable>(
					storeType, cacheConfig.getStoreRegion(), key, cache,
					beanName, cacheMethod);

			cacheProxys.put(key, cacheProxy);

			// 2. ��ʱ��������storeMapCleanTime
			if (StoreType.MAP == storeType
					&& StringUtils.isNotBlank(storeMapCleanTime)) {
				try {
					timeTask.createCleanCacheTask(cacheProxy, storeMapCleanTime);
				} catch (Exception e) {
					log.error("[����]����Map��ʱ��������ʧ��!", e);
				}
			}

			// 3. ע��JMX
			registerCacheMbean(key, cacheProxy, storeMapCleanTime,
					cacheMethod.getExpiredTime());

			// 4. ע��Xray log
			if (openCacheLog)
				cacheProxy.addListener(new XrayLogListener(beanName,
						cacheMethod.getMethodName(), cacheMethod
								.getParameterTypes()));
		}
	}

	/**
	 * ע��JMX
	 * 
	 * @param key
	 * @param cacheProxy
	 */
	private void registerCacheMbean(String key,
			CacheProxy<Serializable, Serializable> cacheProxy,
			String storeMapCleanTime, Integer expiredTime) {
		try {
			String mbeanName = CacheMbean.MBEAN_NAME + ":name=" + key;
			CacheMbeanListener listener = new CacheMbeanListener();
			cacheProxy.addListener(listener);
			CacheMbean<Serializable, Serializable> cacheMbean = new CacheMbean<Serializable, Serializable>(
					cacheProxy, listener, applicationContext,
					storeMapCleanTime, expiredTime);
			MBeanManagerFactory.registerMBean(mbeanName, cacheMbean);
		} catch (InstanceAlreadyExistsException e) {
			log.debug("�ظ�ע��JMX", e);
		} catch (Exception e) {
			log.warn("ע��JMXʧ��", e);
		}
	}

	public CacheProxy<Serializable, Serializable> getCacheProxy(String key) {
		if (key == null || cacheProxys == null)
			return null;

		return cacheProxys.get(key);
	}

	public boolean isUseCache() {
		return useCache;
	}

	@JmxMethod
	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	public void setCacheConfig(CacheConfig cacheConfig) {
		this.cacheConfig = cacheConfig;
	}

	public CacheConfig getCacheConfig() {
		return cacheConfig;
	}

	public void setTairManager(TairManager tairManager) {
		this.tairManager = tairManager;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setLocalMapSize(int localMapSize) {
		this.localMapSize = localMapSize;
	}

	public void setLocalMapSegmentSize(int localMapSegmentSize) {
		this.localMapSegmentSize = localMapSegmentSize;
	}

	public void setOpenCacheLog(boolean openCacheLog) {
		this.openCacheLog = openCacheLog;
	}

}
