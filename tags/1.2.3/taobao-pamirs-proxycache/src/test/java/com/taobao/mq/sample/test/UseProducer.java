package com.taobao.mq.sample.test;


import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.producer.MessageProducer;
import com.taobao.metamorphosis.exception.MetaClientException;


/**
 * ʹ����Ϣ������producer������,���Ͷ��topic��Ϣ,<b>�Ƽ�ʹ��ͬһ��producer</b>
 * 
 * @author �޻�
 * @since 2011-7-15 ����03:33:23
 */

public class UseProducer {

    private String topic1 = "meta-test";

    // inject by spring
    private MessageProducer producer;


    /**
     * ��spring bean�����ó�ʼ������init-method="init" .��spring��������һ���Է���topic(��Ҫ���ö��)
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
            // ������Ҫ����
        }
        catch (InterruptedException e) {
            // ������Ҫ����
        }
    }


    public void setProducer(MessageProducer producer) {
        this.producer = producer;
    }

}
