package com.xiaoshu.controller;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSONObject;
import com.xiaoshu.entity.Tbmajorday;

public class MyMessageListener implements MessageListener {
    
	@Autowired
	private RedisTemplate redisTemplate;
	@Override
	public void onMessage(Message arg0) {
		// TODO Auto-generated method stub
		TextMessage textMessage = (TextMessage) arg0;
		
		try {
			String text = textMessage.getText();
			 Tbmajorday parseObject = JSONObject.parseObject(text,Tbmajorday.class);
			 redisTemplate.boundHashOps("mmm").put(parseObject.getMdname(), parseObject.getMdId());
			 System.out.println(parseObject+"---------------------------------------------------------------------------------");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
