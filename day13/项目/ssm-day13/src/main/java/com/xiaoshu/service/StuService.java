package com.xiaoshu.service;

import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.majortbMapper;
import com.xiaoshu.dao.stutbMapper;
import com.xiaoshu.entity.Majortb;
import com.xiaoshu.entity.StuVo;
import com.xiaoshu.entity.Stutb;
import com.xiaoshu.entity.StutbExample;
import com.xiaoshu.entity.StutbExample.Criteria;


@Service
public class StuService {

	@Autowired
     private stutbMapper stutbMapper;
	
	@Autowired
	private majortbMapper majortbMapper;

	// 新增
	public void addUser(Stutb t) throws Exception {
		stutbMapper.insert(t);
	};

	// 修改
	public void updateUser(Stutb t) throws Exception {
		stutbMapper.updateByPrimaryKeySelective(t);
	};

	// 删除
	public void deleteUser(Integer id) throws Exception {
		stutbMapper.deleteByPrimaryKey(id);
	};


	// 通过用户名判断是否存在，（新增时不能重名）
	public Stutb existUserWithUserName(String userName) throws Exception {
		StutbExample example = new StutbExample();
		Criteria criteria = example.createCriteria();
		criteria.andSNameEqualTo(userName);
		List<Stutb> userList = stutbMapper.selectByExample(example);
		return userList.isEmpty()?null:userList.get(0);
	};

	public PageInfo<StuVo> findUserPage(StuVo stuVo, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<StuVo> userList = stutbMapper.findPage(stuVo);
		PageInfo<StuVo> pageInfo = new PageInfo<StuVo>(userList);
		return pageInfo;
	}

	public List<Majortb> findMajortb() {
		// TODO Auto-generated method stub
		return majortbMapper.selectAll();
	}

	public List<StuVo> findPage(StuVo stuVo) {
		List<StuVo> userList = stutbMapper.findPage(stuVo);
		return userList;
	}

	public List<StuVo> findEcharts(StuVo stuVo) {
		
		return stutbMapper.findEcharts(stuVo);
	}
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private Destination queueTextDestination;
	
	public void addMajor(final Majortb majortb) {
		majortbMapper.insert(majortb);
		
        jmsTemplate.send(queueTextDestination, new MessageCreator() {
			
			@Override
			public Message createMessage(Session arg0) throws JMSException {
				String jsonString = JSONObject.toJSONString(majortb);
				
				return arg0.createTextMessage(jsonString);
			}
		});
	}


}
