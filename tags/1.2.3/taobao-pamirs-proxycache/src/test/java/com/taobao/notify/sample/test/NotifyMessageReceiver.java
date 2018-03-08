package com.taobao.notify.sample.test;

import com.taobao.hsf.notify.extend.MessageReceiver;
import com.taobao.notify.message.Message;
import com.taobao.notify.message.ObjectMessage;
import com.taobao.notify.remotingclient.CheckMessageListener;
import com.taobao.notify.remotingclient.MessageListener;
import com.taobao.notify.remotingclient.MessageStatus;

@SuppressWarnings("rawtypes")
public class NotifyMessageReceiver extends MessageReceiver implements CheckMessageListener,MessageListener{
	
	public void receiveCheckMessage(Message message, MessageStatus status) {   
    
	}	
	public void receiveMessage(Message message, MessageStatus messageStatus) {

		if (message instanceof ObjectMessage ){
			ObjectMessage ObjectMessage = (ObjectMessage)message;
			Object object = ObjectMessage.getObject();
			
			if(object.equals("Hello")){
				System.out.println("收到 CacheManager 发送的 Notify 消息");
			}
		}
		
	}
}
