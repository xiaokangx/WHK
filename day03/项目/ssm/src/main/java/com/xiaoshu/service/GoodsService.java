package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.GoodsMapper;
import com.xiaoshu.dao.GoodstypeMapper;
import com.xiaoshu.entity.Goods;
import com.xiaoshu.entity.GoodsExample;
import com.xiaoshu.entity.GoodsExample.Criteria;
import com.xiaoshu.entity.GoodsVo;
import com.xiaoshu.entity.Goodstype;


@Service
public class GoodsService {

	/*@Autowired
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
    private GoodsMapper goodsMapper;
    
    @Autowired
    private GoodstypeMapper goodstypeMapper;
    
    
	public PageInfo<GoodsVo> findUserPage(GoodsVo goodsVo, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<GoodsVo> userList = goodsMapper.fandPage(goodsVo);
		PageInfo<GoodsVo> pageInfo = new PageInfo<GoodsVo>(userList);
		return pageInfo;
	}
	
	// 新增
		public void addUser(Goods t) throws Exception {
			goodsMapper.insert(t);
		};

		// 修改
		public void updateUser(Goods t) throws Exception {
			goodsMapper.updateByPrimaryKeySelective(t);
		};

		// 删除
		public void deleteUser(Integer id) throws Exception {
			goodsMapper.deleteByPrimaryKey(id);
		};
		
		// 通过用户名判断是否存在，（新增时不能重名）
		public Goods existUserWithUserName(String userName) throws Exception {
			GoodsExample example = new GoodsExample();
			Criteria criteria = example.createCriteria();
			criteria.andNameEqualTo(userName);
			List<Goods> userList = goodsMapper.selectByExample(example);
			return userList.isEmpty()?null:userList.get(0);
		}

		public List<Goodstype> findtype() {
			// TODO Auto-generated method stub
			return goodstypeMapper.selectAll();
		};



}
