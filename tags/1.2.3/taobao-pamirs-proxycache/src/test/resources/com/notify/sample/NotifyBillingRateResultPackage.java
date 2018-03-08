package com.taobao.upp.rating.center.notify;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.common.lang.StringUtil;
import com.taobao.hsf.notify.client.NotifyManagerBean;
import com.taobao.hsf.notify.client.SendResult;
import com.taobao.hsf.notify.client.message.BytesMessage;
import com.taobao.upp.rating.center.jmx.ReNotifyBalanceImpact;
import com.taobao.upp.rating.center.schedule.impl.ScheduleConstant;
import com.taobao.upp.rating.client.dto.billing.BillingImpactPackage;
import com.taobao.upp.rating.client.exception.ExceptionConstant;
import com.taobao.upp.rating.client.exception.RatingEngineException;
import com.taobao.upp.rating.engine.util.HJObjectUtil;

public class NotifyBillingRateResultPackage {
	private final Log log = LogFactory.getLog(NotifyBillingRateResultPackage.class);
	private String topic;
	private String messageType;

	private NotifyManagerBean notifyManagerBean;

	public void setNotifyManagerBean(NotifyManagerBean notifyManagerBean) {
		this.notifyManagerBean = notifyManagerBean;
	}

	/***
	 * 发BillingImpactPackage对象给账务
	 *
	 * @param billImpactObject
	 * @throws RatingEngineException
	 */
	public SendResult sendMessage(BillingImpactPackage billImpactObject)
			throws RatingEngineException {
		BytesMessage message = new BytesMessage();
		Date temp = new Date();
		log.info(temp.getTime() + "load sendMessage--------- begin" + temp);
		byte[] objectBytes = null;
		try {
			if (null == billImpactObject) {
				throw new RatingEngineException(
						ExceptionConstant.RATING_NOTIFY_OBJECT_ISNULL_EXCEPTION,
						"NOTIFY发送对象为空");
			}
			
			if (isInServCodes(billImpactObject.getResImpactDto().get(0).getServiceCode())){
				
				SendResult sendResult = new SendResult();
				sendResult.setSuccess(false);
				log.warn("在服务列表中，不发送：服务code["+ScheduleConstant.getServCode() +"]"+
						"userid:" + billImpactObject.getResImpactDto().get(0).getUserId()+
						"外部订单号" + billImpactObject.getResImpactDto().get(0).getOutTradeNo());
				return sendResult;
			}
			
			objectBytes = HJObjectUtil.getBytesFromObject(billImpactObject);
		} catch (IOException e) {
			throw new RatingEngineException(
					ExceptionConstant.RATING_NOTIFY_EXCEPTION, "", e);
		} catch (RatingEngineException e) {
			throw new RatingEngineException(e.getCode(), e.getMessage(), e);
		} catch (Exception e) {
			throw new RatingEngineException(
					ExceptionConstant.RATE_EXCEPTION_NOT_KNOWN,
					"计费发送NOTIFY消息未知异常", e);
		}
		log.info("load sendMessage--------- step");
		String topic = getTopic();
		String messageType = getMessageType();

		message.setTopic(topic);
		message.setMessageType(messageType);
		message.setBody(objectBytes);
		SendResult rs = notifyManagerBean.sendMessage(message);
		if(!rs.isSuccess()){
			log.error("send is fail. errorMessage:"+ rs.getErrorMessage() +
					"messageId:" + rs.getMessageId());
		}else{
			log.error("send is success:" + rs.isSuccess() + "messageId:" + rs.getMessageId());
		}
		log.info("load sendMessage--------- end");
		return rs;
	}
	
	/**
	 * 判断是否在服务列表中
	 * @return
	 */
	private boolean isInServCodes(String servCode){
		
		if(StringUtil.isEmpty(ReNotifyBalanceImpact.sendServiceCode)){
			return false;
		}
		if (ReNotifyBalanceImpact.sendServiceCode.equalsIgnoreCase("*")) return true;		
		String[] servCodes = ScheduleConstant.getServCode().split(",");
		for (String temServCode:servCodes){
			if (temServCode.equals(servCode)){
				return true;
			}
		}
		
		return false;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public NotifyManagerBean getNotifyManagerBean() {
		return notifyManagerBean;
	}
}
