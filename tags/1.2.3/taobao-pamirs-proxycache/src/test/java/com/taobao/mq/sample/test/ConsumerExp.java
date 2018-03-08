package com.taobao.mq.sample.test;

import java.util.Map;

import com.taobao.metamorphosis.client.consumer.MessageConsumer;
import com.taobao.metamorphosis.client.consumer.MessageListener;
import com.taobao.metamorphosis.exception.MetaClientException;


/**
 * 使用消息生产者producer的例子,接收多个topic消息,<b>推荐使用同一个consumer</b>
 * 
 * @author 无花
 * @since 2011-7-15 下午04:29:14
 */

public class ConsumerExp {

    private MessageConsumer consumer;

    private int maxSize = 1024 * 1024;

    /** 表示每个topic对应的消息处理器 */
    private Map<String/* topic */, MessageListener> listenerMap;


    /**
     * 在spring bean中配置初始化方法init-method="init". 由spring容器调用一次性订阅消息(不要调用多次)
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
            // 这里不要吃掉异常,推荐往外抛
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
