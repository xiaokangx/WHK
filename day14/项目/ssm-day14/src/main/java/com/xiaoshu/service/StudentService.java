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
import com.xiaoshu.dao.StudentMapper;
import com.xiaoshu.dao.TeacherMapper;
import com.xiaoshu.entity.Student;
import com.xiaoshu.entity.StudentExample;
import com.xiaoshu.entity.StudentExample.Criteria;
import com.xiaoshu.entity.StudentVo;
import com.xiaoshu.entity.Teacher;


@Service
public class StudentService {

	@Autowired
	StudentMapper studentMapper;


	// 新增
	public void addUser(Student t) throws Exception {
		studentMapper.insert(t);
	};

	// 修改
	public void updateUser(Student t) throws Exception {
		studentMapper.updateByPrimaryKeySelective(t);
	};

	// 删除
	public void deleteUser(Integer id) throws Exception {
		studentMapper.deleteByPrimaryKey(id);
	};

	// 通过用户名判断是否存在，（新增时不能重名）
	public Student existUserWithUserName(String userName) throws Exception {
		StudentExample example = new StudentExample();
		Criteria criteria = example.createCriteria();
		criteria.andNameEqualTo(userName);
		List<Student> userList = studentMapper.selectByExample(example);
		return userList.isEmpty()?null:userList.get(0);
	};


	public PageInfo<StudentVo> findUserPage(StudentVo studentVo, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<StudentVo> userList = studentMapper.findPage(studentVo);
		PageInfo<StudentVo> pageInfo = new PageInfo<StudentVo>(userList);
		return pageInfo;
	}
     @Autowired
     private TeacherMapper teacherMapper;
     //查询老师
	public List<Teacher> findTeacher() {
		// TODO Auto-generated method stub
		return teacherMapper.selectAll();
	}

	public List<StudentVo> findEchart(StudentVo studentVo) {
		List<StudentVo> findEchart = studentMapper.findEchart( studentVo);
		return findEchart ;
	}
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination queueTextDestination;
	
	
    //添加老师
	public void addTeacher(final Teacher teacher) {
		    teacherMapper.insert(teacher);
		   //int keys = teacherMapper.insertUseGeneratedKeys(teacher);
		   
		   
		jmsTemplate.send(queueTextDestination,new MessageCreator() {
			
			@Override
			public Message createMessage(Session arg0) throws JMSException {
				String jsonString = JSONObject.toJSONString(teacher);
				return arg0.createTextMessage(jsonString);
			}
		});
		
	}

	

    
	

}
