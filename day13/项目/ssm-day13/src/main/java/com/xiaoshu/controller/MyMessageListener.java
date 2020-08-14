package com.xiaoshu.controller;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSONObject;
import com.xiaoshu.entity.Majortb;


public class MyMessageListener implements MessageListener {
	@Autowired
	private RedisTemplate redisTemplate;
	@Override
	public void onMessage(Message arg0) {
		// TODO Auto-generated method stub
		TextMessage textMessage = (TextMessage) arg0;
		
		try {
			String text = textMessage.getText();
			Majortb parseObject = JSONObject.parseObject(text,Majortb.class);
			 redisTemplate.boundHashOps("maj").put(parseObject.getmName(), parseObject.getmId());
			 System.out.println(parseObject+"---------------------------------------------------------------------------------");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
