package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

	@Autowired
	GoodsMapper goodsMapper;

    @Autowired
    private GoodstypeMapper goodstypeMapper;
	
	
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
	};



	public PageInfo<GoodsVo> findUserPage(GoodsVo goodsVo, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<GoodsVo> userList = goodsMapper.findPage(goodsVo);
		PageInfo<GoodsVo> pageInfo = new PageInfo<GoodsVo>(userList);
		return pageInfo;
	}

	public List<Goodstype> findGoodstype() {
		// TODO Auto-generated method stub
		return goodstypeMapper.selectAll();
	}
    @Autowired
    RedisTemplate redisTemplate;
    
    //添加分类
	public void addTypeGoods(Goodstype goodstype) {
		goodstypeMapper.insert(goodstype);
		redisTemplate.delete("tList");
	}

	public List<GoodsVo> findEcharts() {
		// TODO Auto-generated method stub
		return goodsMapper.findEcharts();
	}


}
