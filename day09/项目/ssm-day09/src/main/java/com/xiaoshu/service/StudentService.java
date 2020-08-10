package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.CourseMapper;
import com.xiaoshu.dao.StudentMapper;
import com.xiaoshu.entity.Course;
import com.xiaoshu.entity.Student;
import com.xiaoshu.entity.StudentExample;
import com.xiaoshu.entity.StudentExample.Criteria;
import com.xiaoshu.entity.StudentVo;

@Service
public class StudentService {

	@Autowired
	StudentMapper studentMapper;
    
	@Autowired
	CourseMapper courseMapper;

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

	public List<Course> findCourse() {
		// TODO Auto-generated method stub
		return courseMapper.selectAll();
	}


	public List<StudentVo> findPage(StudentVo studentVo) {
		List<StudentVo> userList = studentMapper.findPage(studentVo);
		return userList;
	}

	public List<StudentVo> findEcharts() {
		// TODO Auto-generated method stub
		return studentMapper.findEcharts();
	}

	public void addCourse(Course course) {
		// TODO Auto-generated method stub
		courseMapper.insert(course);
	}

	public int findById(String cname) {
		Course course = new Course();
		course.setName(cname);
		// TODO Auto-generated method stub
		Course one = courseMapper.selectOne(course);
		return one.getId();
	}




	


}
