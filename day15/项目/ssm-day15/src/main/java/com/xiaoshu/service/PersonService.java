package com.xiaoshu.service;

import java.util.List;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.ibatis.annotations.Options;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.JiyunBankMapper;
import com.xiaoshu.dao.JiyunPersonMapper;
import com.xiaoshu.entity.JiyunBank;
import com.xiaoshu.entity.JiyunPerson;
import com.xiaoshu.entity.JiyunPersonExample;
import com.xiaoshu.entity.JiyunPersonExample.Criteria;
import com.xiaoshu.entity.PersonVo;


@Service
public class PersonService {

	@Autowired
	JiyunPersonMapper personMapper;
    @Autowired
    private JiyunBankMapper bankMapper;
	

	// 新增
	public void addUser(JiyunPerson t) throws Exception {
		personMapper.insert(t);
	};

	// 修改
	public void updateUser(JiyunPerson t) throws Exception {
		personMapper.updateByPrimaryKeySelective(t);
	};

	// 删除
	public void deleteUser(Integer id) throws Exception {
		personMapper.deleteByPrimaryKey(id);
	};

	

	// 通过用户名判断是否存在，（新增时不能重名）
	public JiyunPerson existUserWithUserName(String userName) throws Exception {
		JiyunPersonExample example = new JiyunPersonExample();
		Criteria criteria = example.createCriteria();
		criteria.andPNameEqualTo(userName);
		List<JiyunPerson> userList = personMapper.selectByExample(example);
		return userList.isEmpty()?null:userList.get(0);
	};


	public PageInfo<PersonVo> findUserPage(PersonVo personVo, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<PersonVo> userList = personMapper.findPage(personVo);
		PageInfo<PersonVo> pageInfo = new PageInfo<PersonVo>(userList);
		return pageInfo;
	}
    //查询银行
	public List<JiyunBank> findJiyunBank() {
		// TODO Auto-generated method stub
		return bankMapper.selectAll();
	}

	public List<PersonVo> findPage(PersonVo personVo) {
		List<PersonVo> userList = personMapper.findPage(personVo);
		return userList;
	}

	public List<PersonVo> findEcharts() {
		
		return personMapper.findEcharts();
	}
    @Autowired
    private JmsTemplate jmsTemplate;
	@Autowired
	private Destination queueTextDestination;
	
	public void addBank(final JiyunBank jiyunBank) {
		bankMapper.insert(jiyunBank);
		
		jmsTemplate.send(queueTextDestination,new MessageCreator() {
			
			@Override
			public Message createMessage(Session arg0) throws JMSException {
				String jsonString = JSONObject.toJSONString(jiyunBank);
				return arg0.createTextMessage(jsonString);
			}
		});
	}

	
    
	

}
