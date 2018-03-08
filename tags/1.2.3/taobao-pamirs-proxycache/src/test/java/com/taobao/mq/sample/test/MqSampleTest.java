package com.taobao.mq.sample.test;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

//@SpringApplicationContext({ "com\\taobao\\mq\\sample\\test\\mqConfig.xml" })
@SpringApplicationContext({ "mqConfig.xml" })
public class MqSampleTest extends UnitilsJUnit4 {
	
	
	@SpringBeanByName
	ConsumerExp consumerExp;
	public void setConsumerExp(ConsumerExp consumerExp){
		this.consumerExp = consumerExp;
	}
	
	@SpringBeanByName
	UseProducer useProducer;
	public void setUseProducer(UseProducer useProducer){
		this.useProducer = useProducer;
	}	
	
	@Test 	
	public void testProductor(){
		
		System.out.println("consumerExp" + consumerExp);
		System.out.println("useProducer" + useProducer);
		
		byte[] byteArray = {0,0,1};
		
		useProducer.sendMessageForTopic1(byteArray);
		
		System.out.println(System.currentTimeMillis());
	}
}