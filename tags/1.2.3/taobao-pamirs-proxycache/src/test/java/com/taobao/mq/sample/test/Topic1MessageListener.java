package com.taobao.mq.sample.test;

import java.util.concurrent.Executor;

import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.consumer.MessageListener;


/**
 * 
 * @author �޻�
 * @since 2011-7-15 ����04:49:14
 */

public class Topic1MessageListener implements MessageListener {

    public Executor getExecutor() {
        return null;
    }


    // �յ���Ϣʱ��ص��������
    public void recieveMessages(Message message) {
        // ������Ҫ������Ϣ
        System.out.println("Receive message " + new String(message.getData()));
    }

}
