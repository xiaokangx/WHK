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
import com.xiaoshu.dao.TbmajordayMapper;
import com.xiaoshu.dao.TbstudayMapper;
import com.xiaoshu.entity.StuVo;
import com.xiaoshu.entity.Tbmajorday;
import com.xiaoshu.entity.Tbstuday;
import com.xiaoshu.entity.TbstudayExample;
import com.xiaoshu.entity.TbstudayExample.Criteria;


@Service
public class StuService {

	@Autowired
	TbstudayMapper tbstudayMapper;
	

	

	// 新增
	public void addUser(Tbstuday t) throws Exception {
		tbstudayMapper.insert(t);
	};

	// 修改
	public void updateUser(Tbstuday t) throws Exception {
		tbstudayMapper.updateByPrimaryKeySelective(t);
	};

	// 删除
	public void deleteUser(Integer id) throws Exception {
		tbstudayMapper.deleteByPrimaryKey(id);
	};

	// 通过用户名判断是否存在，（新增时不能重名）
	public Tbstuday existUserWithUserName(String userName) throws Exception {
		TbstudayExample example = new TbstudayExample();
		Criteria criteria = example.createCriteria();
		criteria.andSdnameEqualTo(userName);
		List<Tbstuday> userList = tbstudayMapper.selectByExample(example);
		return userList.isEmpty()?null:userList.get(0);
	};

	public PageInfo<StuVo> findUserPage(StuVo stuVo, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<StuVo> userList = tbstudayMapper.findPage(stuVo);
		PageInfo<StuVo> pageInfo = new PageInfo<StuVo>(userList);
		return pageInfo;
	}
	@Autowired
	private TbmajordayMapper tbmajordayMapper;

	public List<Tbmajorday> findTbmajorday() {
		// TODO Auto-generated method stub
		return tbmajordayMapper.selectAll();
	}

	public List<StuVo> findPage(StuVo stuVo) {
		List<StuVo> userList = tbstudayMapper.findPage(stuVo);
		return userList;
	}

	public List<StuVo> findecharts(StuVo stuVo) {
		return tbstudayMapper.findecharts(stuVo);
	}

	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination queueTextDestination;
	
    //添加部门
	public void addMajor(final Tbmajorday tbmajorday) {
		 tbmajordayMapper.insert(tbmajorday);
		
		 jmsTemplate.send(queueTextDestination, new MessageCreator() {
			
			@Override
			public Message createMessage(Session arg0) throws JMSException {
				String jsonString = JSONObject.toJSONString(tbmajorday);
				
				return arg0.createTextMessage(jsonString);
			}
		});
	}


}
