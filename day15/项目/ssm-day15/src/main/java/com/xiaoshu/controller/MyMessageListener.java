package com.xiaoshu.controller;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.alibaba.fastjson.JSONObject;
import com.xiaoshu.entity.JiyunBank;

public class MyMessageListener implements MessageListener{

	@Override
	public void onMessage(Message arg0) {
		// TODO Auto-generated method stub
		TextMessage textMessage = (TextMessage) arg0;
		try {
			String text = textMessage.getText();
			JiyunBank parseObject = JSONObject.parseObject(text,JiyunBank.class);
			System.out.println("++++++++++++++++++++++++++"+parseObject);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
