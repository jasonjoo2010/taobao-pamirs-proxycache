package com.taobao.mq.sample.test;

import java.util.Map;

import com.taobao.metamorphosis.client.consumer.MessageConsumer;
import com.taobao.metamorphosis.client.consumer.MessageListener;
import com.taobao.metamorphosis.exception.MetaClientException;


/**
 * ʹ����Ϣ������producer������,���ն��topic��Ϣ,<b>�Ƽ�ʹ��ͬһ��consumer</b>
 * 
 * @author �޻�
 * @since 2011-7-15 ����04:29:14
 */

public class ConsumerExp {

    private MessageConsumer consumer;

    private int maxSize = 1024 * 1024;

    /** ��ʾÿ��topic��Ӧ����Ϣ������ */
    private Map<String/* topic */, MessageListener> listenerMap;


    /**
     * ��spring bean�����ó�ʼ������init-method="init". ��spring��������һ���Զ�����Ϣ(��Ҫ���ö��)
     */
    public void init() {
        try {
            for (Map.Entry<String, MessageListener> entry : this.listenerMap.entrySet()) {
                consumer.subscribe(entry.getKey(), this.maxSize, entry.getValue());
            }

            consumer.completeSubscribe();
        }
        catch (MetaClientException e) {
        	e.printStackTrace();
            // ���ﲻҪ�Ե��쳣,�Ƽ�������
            // log.errer
            // throw new runtimeexception
        }
    }


    public void setConsumer(MessageConsumer consumer) {
        this.consumer = consumer;
    }

    public void setListenerMap(Map<String, MessageListener> listenerMap) {
        this.listenerMap = listenerMap;
    }

}
