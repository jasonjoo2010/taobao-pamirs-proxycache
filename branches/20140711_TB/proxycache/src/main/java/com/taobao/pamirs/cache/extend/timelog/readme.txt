====== ����ʱ����־���� ======
һ��ʹ�ó���
      ���ԡ���ӡָ��bean�ķ�������ʱ�䣬������method�켣չʾ

��������
1.	Ŀ�������������
2.	����������Ӧʱ���ӡ
3.	�������ù켣չʾ
4.	֧�ַ���������ӡ�����أ�
5.  ֧��Annotation���������ַ�ʽ����
6.	֧�ֽű�ͳ�ƽӿڵ��ô�����sort��
	cat 9403.txt |awk '{FS=":"; print $1}'|sort -n |uniq -c|sort -n > 9403.z.txt


����ʹ�÷���
1. ֻ��ע��
	@TimeLog

2. ע��spring bean����ѡ��
	��һЩҪ��ӡ��bean�ǵ��������ģ����ܼ�ע�⣬����ѡ������÷�ʽ
	������Բ���scan��ʽ������
		<context:component-scan base-package="com.taobao.pamirs.cache.extend.timelog" />

	<bean class="com.taobao.pamirs.cache.extend.timelog.TimeHandle">
		<property name="beanList">
			<list>
				<value>articleReadClient</value>
				<value>itemReadClient</value>
				<value>packReadClient</value>
				<value>prodSubscriptionService</value>
				<value>bizOrderCommonService</value>
				<value>servReadService</value>
				<value>accumulationQueryUtil</value>
				<value>productReadService</value>
				<value>resourceLimitUtil</value>
				<value>alipayInfoQueryUtil</value>
				<value>bizVerifyService</value>
				<value>subParameterService</value>
			</list>
		</property>
	</bean>

3. ��ӡ����ʱ����־��for log4j��

     <appender name="timelog" class="org.apache.log4j.DailyRollingFileAppender">
        <param name="file" value="${loggingRoot}/timelog.log"/>
        <param name="append" value="false"/>
        <param name="encoding" value="GBK"/>  
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d [%X{requestURIWithQueryString}] %-5p %c{2} - %m%n"/>
        </layout>
    </appender>
    
    <logger name="com.taobao.pamirs.cache.extend.timelog" additivity="false">
        <level value="warn"/>
        <appender-ref ref="timelog"/>
    </logger>