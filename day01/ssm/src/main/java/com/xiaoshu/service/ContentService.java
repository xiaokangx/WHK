package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.ContentMapper;
import com.xiaoshu.dao.ContentcategoryMapper;
import com.xiaoshu.entity.Content;
import com.xiaoshu.entity.ContentExample;
import com.xiaoshu.entity.ContentExample.Criteria;
import com.xiaoshu.entity.ContentVo;
import com.xiaoshu.entity.Contentcategory;

@Service
public class ContentService {

/*	@Autowired
	UserMapper userMapper;

	// 查询所有
	public List<User> findUser(User t) throws Exception {
		return userMapper.select(t);
	};

	// 数量
	public int countUser(User t) throws Exception {
		return userMapper.selectCount(t);
	};

	// 通过ID查询
	public User findOneUser(Integer id) throws Exception {
		return userMapper.selectByPrimaryKey(id);
	};

	

	// 登录
	public User loginUser(User user) throws Exception {
		UserExample example = new UserExample();
		Criteria criteria = example.createCriteria();
		criteria.andPasswordEqualTo(user.getPassword()).andUsernameEqualTo(user.getUsername());
		List<User> userList = userMapper.selectByExample(example);
		return userList.isEmpty()?null:userList.get(0);
	};

	

	// 通过角色判断是否存在
	public User existUserWithRoleId(Integer roleId) throws Exception {
		UserExample example = new UserExample();
		Criteria criteria = example.createCriteria();
		criteria.andRoleidEqualTo(roleId);
		List<User> userList = userMapper.selectByExample(example);
		return userList.isEmpty()?null:userList.get(0);
	}*/
     
	@Autowired
	private ContentMapper contentMapper;
	
	@Autowired
	private ContentcategoryMapper contentcategoryMapper;
	
	public PageInfo<ContentVo> findUserPage(ContentVo contentVo, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		ContentExample example = new ContentExample();
		Criteria criteria = example.createCriteria();
		List<ContentVo> userList = contentMapper.findPage(contentVo);
		PageInfo<ContentVo> pageInfo = new PageInfo<ContentVo>(userList);
		return pageInfo;
	}
    //广告分类查询
	public List<Contentcategory> findCategory() {
		return contentcategoryMapper.selectAll();
	}
    
	// 新增
		public void addUser(Content t) throws Exception {
			contentMapper.insert(t);
		};

		// 修改
		public void updateUser(Content t) throws Exception {
			contentMapper.updateByPrimaryKeySelective(t);
		};

		// 删除
		public void deleteUser(Integer id) throws Exception {
			contentMapper.deleteByPrimaryKey(id);
		};
		
		// 通过用户名判断是否存在，（新增时不能重名）
		public Content existUserWithUserName(String userName) throws Exception {
			ContentExample example = new ContentExample();
			Criteria criteria = example.createCriteria();
			criteria.andContenttitleEqualTo(userName);
			List<Content> userList = contentMapper.selectByExample(example);
			return userList.isEmpty()?null:userList.get(0);
		};
		
		public List<ContentVo> findPage(ContentVo ContentVo) {
			// TODO Auto-generated method stub
			List<ContentVo> userList = contentMapper.findPage(ContentVo);
			return userList;
		}

}
