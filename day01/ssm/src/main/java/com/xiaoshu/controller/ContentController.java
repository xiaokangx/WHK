package com.xiaoshu.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.dao.ContentcategoryMapper;
import com.xiaoshu.entity.Content;
import com.xiaoshu.entity.ContentVo;
import com.xiaoshu.entity.Contentcategory;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.Role;
import com.xiaoshu.entity.User;
import com.xiaoshu.service.ContentService;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.RoleService;
import com.xiaoshu.service.UserService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.WriterUtil;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("content")
public class ContentController extends LogController{
	static Logger logger = Logger.getLogger(ContentController.class);

	@Autowired
	private ContentService contentService;
	
	@Autowired
	private ContentcategoryMapper contentcategoryMapper;
	
	@Autowired
	private RoleService roleService ;
	
	@Autowired
	private OperationService operationService;
	
	
	@RequestMapping("contentIndex")
	public String index(HttpServletRequest request,Integer menuid) throws Exception{
		List<Role> roleList = roleService.findRole(new Role());
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		
		List<Contentcategory> caList = contentService.findCategory();
		request.setAttribute("caList", caList);
		
		request.setAttribute("operationList", operationList);
		request.setAttribute("roleList", roleList);
		return "content";
	}
	
	
	@RequestMapping(value="contentList",method=RequestMethod.POST)
	public void contentList(ContentVo contentVo,HttpServletRequest request,HttpServletResponse response,String offset,String limit) throws Exception{
		
		try {
			
			Integer pageSize = StringUtil.isEmpty(limit)?ConfigUtil.getPageSize():Integer.parseInt(limit);
			Integer pageNum =  (Integer.parseInt(offset)/pageSize)+1;
			PageInfo<ContentVo> userList= contentService.findUserPage(contentVo,pageNum,pageSize);
			
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
	@RequestMapping("reserveContent")
	public void reserveContent(HttpServletRequest request,Content content,HttpServletResponse response){
		Integer userId = content.getContentid();
		JSONObject result=new JSONObject();
		try {
			if (userId != null) {   // userId不为空 说明是修改
				Content userName = contentService.existUserWithUserName(content.getContenttitle());
				/*if(userName != null && userName.getContentid().compareTo(userId)==0){
					content.setCreatetime(new Date());
					content.setContentid(userId);
					contentService.updateUser(content);
					result.put("success", true);
				}else{
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}*/
				
				if(userName == null){
					content.setCreatetime(new Date());
					content.setContentid(userId);
					contentService.updateUser(content);
					result.put("success", true);
				}else{
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}
				
			}else {   // 添加
				if(contentService.existUserWithUserName(content.getContenttitle())==null){  // 没有重复可以添加
					content.setCreatetime(new Date());
					contentService.addUser(content);
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
	
	
	@RequestMapping("deleteContent")
	public void deleteContent(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");
			for (String id : ids) {
				contentService.deleteUser(Integer.parseInt(id));
			}
			result.put("success", true);
			result.put("delNums", ids.length);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除用户信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
	//导入
		@RequestMapping("importContent")
		public void importContent(MultipartFile impFile,HttpServletRequest request,HttpServletResponse response){
			JSONObject result=new JSONObject();
			try {
				//获取excel文件
				HSSFWorkbook wb = new HSSFWorkbook(impFile.getInputStream());
				//获取文件中的sheet页
				HSSFSheet sheetAt = wb.getSheetAt(0);
				//获取最后一行行数
				int rowNum = sheetAt.getLastRowNum();
				//循环行数获取每一行对象
				for (int i = 1; i <= rowNum; i++) {
					//获取每一行单元格
					HSSFRow row = sheetAt.getRow(i);
					
					Double numericCellValue = row.getCell(0).getNumericCellValue();
					int contentid =numericCellValue.intValue();
					
					String contenttitle = row.getCell(1).getStringCellValue();
					
					String aname = row.getCell(2).getStringCellValue();
					
					String picpath = row.getCell(3).getStringCellValue();
					
					String contenturl = row.getCell(4).getStringCellValue();
					
					Double price1 = row.getCell(5).getNumericCellValue();
					int price = price1.intValue();
					
					String status = row.getCell(6).getStringCellValue();
					
					Date createtime = row.getCell(7).getDateCellValue();
					//根据部门名查部门id
					Integer contentcategoryid =  findCategoryIdByAname(aname);
					
					
					//封装emp对象
					Content content = new Content();
					content.setContentid(contentid);
					content.setContenttitle(contenttitle);
					content.setContentcategoryid(contentcategoryid);
					content.setPicpath(picpath);
					content.setContenturl(contenturl);
					content.setPrice(price);
					content.setStatus(status);
					content.setCreatetime(createtime);
					//调用service保存方法和数据
					contentService.addUser(content);
					
				}
				
				result.put("success", true);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("删除用户信息错误",e);
				result.put("errorMsg", "对不起，删除失败");
			}
			WriterUtil.write(response, result.toString());
		}


		private Integer findCategoryIdByAname(String aname) {
			Contentcategory contentcategory = new Contentcategory(); 
			contentcategory.setCategoryname(aname);
			Contentcategory one = contentcategoryMapper.selectOne(contentcategory);
			if(one==null){
				contentcategoryMapper.insert(contentcategory);
				one=contentcategory;
			}
			
			return one.getContentcategoryid();
		}

}
