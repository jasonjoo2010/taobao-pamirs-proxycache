====== ��̬�̻߳��� ======
һ��ʹ�ó���
    �������У����̣߳����漰���Զ��method�ظ����ã����ҽ����ͬ�������в���DML�����
    ��ʹ�����ӿ����ܸܺߣ�Ҳ�ᵼ�������������ܽ��͡�
    	���磺
    		����������Ҫ������Ʒ�ӿ�100�����ϣ���Ȼÿ�ε���ֻ��Ҫ3ms������tair�������ܵ�RTҲ��Ҫ300ms��
    		��
    Ϊ�������ܣ����ֲ����ԭ�еĴ�����������
    	���磺
    		1. ���ظ����õ�method��ǰ���ã�Ȼ��ѽ�����ݸ�ÿһ�����õĵط�
    		2. ��������threadlocal���������棬Ȼ��ÿ�������ڲ��޸��߼�����������ȡ����
    		��

����Ŀ��
1. ��ԭ�д��������������
2. ��spring�޷���
3. ֧��method����
4. ֧��bean��method = 1��n����
5. lazy load���Լ��ػ���
6. �����ʴ�ӡ
7. JMX��̬����


����ʹ�÷���
1. ע��spring bean��

	<bean class="com.taobao.pamirs.cache.store.threadcache.ThreadCacheHandle">
		<property name="beansMap">
		<!-- the void method not support�� will ignore cache -->
			<map>
				<entry key="articleReadClient" value="getArticleById,getPromotionIds,getMarketSaleConditions" />
				<entry key="itemReadClient" value="getItemById,getMutexItemIds,getPromotionIds,getSaleConditions" />
				<entry key="prodSubscriptionService" value="countProdSubNumByProductId" />
				<entry key="bizVerifyService" value="existAppstoreSubParam" />
			</map>		
		</property>
	</bean>
	
2. ���̿�ʼʱ�����̻߳���
	public void process() {
		// �̻߳�������
		ThreadContext.startLocalCache();
		try {
			...
		} finally {
			ThreadContext.remove();	
		}
	}

2. ��ӡcache������־����ѡ�� for log4j

     <appender name="threadcache" class="org.apache.log4j.DailyRollingFileAppender">
        <param name="file" value="${loggingRoot}/threadcache.log"/>
        <param name="append" value="false"/>
        <param name="encoding" value="GBK"/>  
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d [%X{requestURIWithQueryString}] %-5p %c{2} - %m%n"/>
        </layout>
    </appender>
    
    <logger name="com.taobao.pamirs.cache.store.threadcache" additivity="false">
        <level value="warn"/>
        <appender-ref ref="threadcache"/>
    </logger>