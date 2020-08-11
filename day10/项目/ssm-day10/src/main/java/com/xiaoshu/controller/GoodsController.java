package com.xiaoshu.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.entity.Goods;
import com.xiaoshu.entity.GoodsVo;
import com.xiaoshu.entity.Goodstype;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.Role;
import com.xiaoshu.entity.User;
import com.xiaoshu.service.GoodsService;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.RoleService;
import com.xiaoshu.service.UserService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.WriterUtil;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("goods")
public class GoodsController extends LogController{
	static Logger logger = Logger.getLogger(GoodsController.class);

	@Autowired
	private GoodsService goodsService;
	
	@Autowired
	private RoleService roleService ;
	
	@Autowired
	private OperationService operationService;
	
	@Autowired
	RedisTemplate redisTemplate;
	@RequestMapping("goodsIndex")
	public String index(HttpServletRequest request,Integer menuid) throws Exception{
		List<Role> roleList = roleService.findRole(new Role());
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		
		List<Goodstype> tList = (List<Goodstype>) redisTemplate.boundHashOps("tList").get("goodstype");
		if(tList==null){
			tList = goodsService.findGoodstype();
			redisTemplate.boundHashOps("tList").put("goodstype", tList);
		}else {
			System.out.println("---从redis中查---");
		}
		
		request.setAttribute("tList", tList);
		
		request.setAttribute("operationList", operationList);
		request.setAttribute("roleList", roleList);
		return "goods";
	}
	
	
	@RequestMapping(value="goodsList",method=RequestMethod.POST)
	public void goodsList(GoodsVo goodsVo,HttpServletRequest request,HttpServletResponse response,String offset,String limit) throws Exception{
		try {
			
			Integer pageSize = StringUtil.isEmpty(limit)?ConfigUtil.getPageSize():Integer.parseInt(limit);
			Integer pageNum =  (Integer.parseInt(offset)/pageSize)+1;
			PageInfo<GoodsVo> userList= goodsService.findUserPage(goodsVo,pageNum,pageSize);
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("total",userList.getTotal() );
			jsonObj.put("rows", userList.getList());
	        WriterUtil.write(response,jsonObj.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户展示错误",e);
			throw e;
		}
	}
	
	
	// 新增或修改
	@RequestMapping("reserveGoods")
	public void reserveGoods(HttpServletRequest request,Goods goods,HttpServletResponse response){
		Integer userId = goods.getId();
		JSONObject result=new JSONObject();
		try {
			if (userId != null) {   // userId不为空 说明是修改
				Goods userName = goodsService.existUserWithUserName(goods.getName());
				/*if(userName != null && userName.getId().compareTo(userId)==0){
					goods.setCreatetime(new Date());
					goods.setId(userId);
					goodsService.updateUser(goods);
					result.put("success", true);
				}else{
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}*/
				
				if(userName == null){
					goods.setCreatetime(new Date());
					goods.setId(userId);
					goodsService.updateUser(goods);
					result.put("success", true);
				}else{
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}
				
				
			}else {   // 添加
				if(goodsService.existUserWithUserName(goods.getName())==null){  // 没有重复可以添加
					goods.setCreatetime(new Date());
					goodsService.addUser(goods);
					result.put("success", true);
				} else {
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存用户信息错误",e);
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	// 添加分类
		@RequestMapping("addtypeGoods")
		public void addtypeGoods(HttpServletRequest request,Goodstype goodstype,HttpServletResponse response){
			JSONObject result=new JSONObject();
			try {
				
					 goodsService.addTypeGoods(goodstype);
					
					 result.put("success", true);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("保存用户信息错误",e);
				result.put("success", true);
				result.put("errorMsg", "对不起，操作失败");
			}
			WriterUtil.write(response, result.toString());
		}
		
		//报表
		@RequestMapping("typeecharts")
		public void typeecharts(HttpServletRequest request,HttpServletResponse response){
			JSONObject result=new JSONObject();
			try {
				List<GoodsVo> elist = goodsService.findEcharts();
				result.put("elist", elist);
				result.put("success", true);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("删除用户信息错误",e);
				result.put("errorMsg", "对不起，删除失败");
			}
			WriterUtil.write(response, result.toString());
		}
	

}
