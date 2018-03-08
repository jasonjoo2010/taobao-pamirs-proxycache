package com.taobao.mq.sample.test;


import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.producer.MessageProducer;
import com.taobao.metamorphosis.exception.MetaClientException;


/**
 * 使用消息生产者producer的例子,发送多个topic消息,<b>推荐使用同一个producer</b>
 * 
 * @author 无花
 * @since 2011-7-15 下午03:33:23
 */

public class UseProducer {

    private String topic1 = "meta-test";

    // inject by spring
    private MessageProducer producer;


    /**
     * 在spring bean中配置初始化方法init-method="init" .由spring容器调用一次性发布topic(不要调用多次)
     */
    public void init() {
        producer.publish(this.topic1);;
    }


    public void sendMessageForTopic1(byte[] data) {
        sendMessage(this.topic1, data);
    }


    private void sendMessage(String topic, byte[] data) {
        try {
            producer.sendMessage(new Message(topic, data));
        }
        catch (MetaClientException e) {
            // 根据需要处理
        }
        catch (InterruptedException e) {
            // 根据需要处理
        }
    }


    public void setProducer(MessageProducer producer) {
        this.producer = producer;
    }

}
