package com.taobao.upp.switching.notify;

import java.util.ArrayList;
import java.util.List;

import com.taobao.hsf.notify.client.message.Message;
import com.taobao.hsf.notify.client.CheckMessageListener;
import com.taobao.hsf.notify.extend.CheckMessageWorker;
import com.taobao.hsf.notify.extend.MessageReceiver;
import com.taobao.hsf.notify.extend.MessageWorker;
import com.taobao.hsf.notify.client.MessageStatus;
/**
 * Swtich Notify消息接收体
 * @since : 2010-11-29
 * @version : 1.0
 */
@SuppressWarnings("rawtypes")
public class SwitchMessageReceiver extends MessageReceiver implements CheckMessageListener{

	
	private List<MessageWorker> list;
	private List<CheckMessageWorker> checkList;
	
	public void init(){
		if(list==null||list.size()<=0){
			list = new ArrayList<MessageWorker>();
		}
		for(MessageWorker mw:list){
			super.addMessageHandler(mw);
		}
	}
	
	public void receiveCheckMessage(Message message, MessageStatus status) {   
    }

	public List<MessageWorker> getList() {
		return list;
	}

	public void setList(List<MessageWorker> list) {
		this.list = list;
	}

	public List<CheckMessageWorker> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<CheckMessageWorker> checkList) {
		this.checkList = checkList;
	}
}
