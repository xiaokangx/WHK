package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.CompanyMapper;
import com.xiaoshu.dao.PersonMapper;
import com.xiaoshu.entity.Company;
import com.xiaoshu.entity.Person;
import com.xiaoshu.entity.PersonExample;
import com.xiaoshu.entity.PersonExample.Criteria;
import com.xiaoshu.entity.PersonVo;

@Service
public class PersonService {

	@Autowired
	PersonMapper personMapper;
	
	@Autowired
	private CompanyMapper companyMapper;

	// 新增
	public void addUser(Person t) throws Exception {
		personMapper.insert(t);
	};

	// 修改
	public void updateUser(Person t) throws Exception {
		personMapper.updateByPrimaryKeySelective(t);
	};

	// 删除
	public void deleteUser(Integer id) throws Exception {
		personMapper.deleteByPrimaryKey(id);
	};

	
	// 通过用户名判断是否存在，（新增时不能重名）
	public Person existUserWithUserName(String userName) throws Exception {
		PersonExample example = new PersonExample();
		Criteria criteria = example.createCriteria();
		criteria.andExpressNameEqualTo(userName);
		List<Person> userList = personMapper.selectByExample(example);
		return userList.isEmpty()?null:userList.get(0);
	};

	

	public PageInfo<PersonVo> findUserPage(PersonVo personVo, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<PersonVo> userList = personMapper.findPage(personVo);
		PageInfo<PersonVo> pageInfo = new PageInfo<PersonVo>(userList);
		return pageInfo;
	}

	public List<Company> findCompany() {
		// TODO Auto-generated method stub
		return companyMapper.selectAll();
	}

	public List<PersonVo> findPage(PersonVo personVo) {
		List<PersonVo> userList = personMapper.findPage(personVo);
		return userList;
	}


}
