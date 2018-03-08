package com.taobao.mq.sample.test;

import java.util.concurrent.Executor;

import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.consumer.MessageListener;


/**
 * 
 * @author 无花
 * @since 2011-7-15 下午04:49:14
 */

public class Topic1MessageListener implements MessageListener {

    public Executor getExecutor() {
        return null;
    }


    // 收到消息时会回调这个方法
    public void recieveMessages(Message message) {
        // 根据需要处理消息
        System.out.println("Receive message " + new String(message.getData()));
    }

}
