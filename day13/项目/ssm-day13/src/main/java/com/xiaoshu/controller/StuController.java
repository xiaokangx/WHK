package com.xiaoshu.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.dao.majortbMapper;
import com.xiaoshu.entity.Attachment;
import com.xiaoshu.entity.Log;
import com.xiaoshu.entity.Majortb;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.Role;
import com.xiaoshu.entity.StuVo;
import com.xiaoshu.entity.Stutb;
import com.xiaoshu.entity.User;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.RoleService;
import com.xiaoshu.service.StuService;
import com.xiaoshu.service.UserService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.TimeUtil;
import com.xiaoshu.util.WriterUtil;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("stu")
public class StuController extends LogController{
	static Logger logger = Logger.getLogger(StuController.class);

	@Autowired
	private StuService stuService;
	
	@Autowired
	private RoleService roleService ;
	
	@Autowired
	private OperationService operationService;
	
	
	@RequestMapping("stuIndex")
	public String stuIndex(HttpServletRequest request,Integer menuid) throws Exception{
		List<Role> roleList = roleService.findRole(new Role());
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		
		List<Majortb> mList = stuService.findMajortb();
		request.setAttribute("mList", mList);
		
		request.setAttribute("operationList", operationList);
		request.setAttribute("roleList", roleList);
		return "stu";
	}
	
	
	@RequestMapping(value="stuList",method=RequestMethod.POST)
	public void stuList(StuVo stuVo,HttpServletRequest request,HttpServletResponse response,String offset,String limit) throws Exception{
		try {
			Integer pageSize = StringUtil.isEmpty(limit)?ConfigUtil.getPageSize():Integer.parseInt(limit);
			Integer pageNum =  (Integer.parseInt(offset)/pageSize)+1;
			PageInfo<StuVo> userList= stuService.findUserPage(stuVo,pageNum,pageSize);
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
	@RequestMapping("reserveStu")
	public void reserveUser(HttpServletRequest request,Stutb stutb,HttpServletResponse response){
		Integer userId = stutb.getsId();
		JSONObject result=new JSONObject();
		try {
			if (userId != null) {   // userId不为空 说明是修改
				Stutb userName = stuService.existUserWithUserName(stutb.getsName());
				/*if(userName != null && userName.getsId().compareTo(userId)==0){
					stutb.setsId(userId);
					stuService.updateUser(stutb);
					result.put("success", true);
				}else{
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}*/
				
				if(userName == null){
					stutb.setsId(userId);
					stuService.updateUser(stutb);
					result.put("success", true);
				}else{
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}
			}else {   // 添加
				if(stuService.existUserWithUserName(stutb.getsName())==null){  // 没有重复可以添加
					stuService.addUser(stutb);
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
	
	// 添加专业
		@RequestMapping("addMajor")
		public void addMajor(HttpServletRequest request,Majortb majortb,HttpServletResponse response){
			JSONObject result=new JSONObject();
			try {
				
				stuService.addMajor(majortb);
				
				
				result.put("success", true);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("保存用户信息错误",e);
				result.put("success", true);
				result.put("errorMsg", "对不起，操作失败");
			}
			WriterUtil.write(response, result.toString());
		}

	
	//删除
	@RequestMapping("deleteStu")
	public void deleteStu(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");
			for (String id : ids) {
				stuService.deleteUser(Integer.parseInt(id));
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
	//报表
	@RequestMapping("addEcharts")
	public void addEcharts(StuVo stuVo ,HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			
			List<StuVo> alist = stuService.findEcharts(stuVo);
			result.put("alist", alist);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除用户信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
	
	//导入
	@RequestMapping("importStu")
	public void importStu(MultipartFile importStu ,HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			HSSFWorkbook hssfWorkbook = new HSSFWorkbook(importStu.getInputStream());
			HSSFSheet sheetAt = hssfWorkbook.getSheetAt(0);
			int rowNum = sheetAt.getLastRowNum();
			for (int i = 1; i < rowNum; i++) {
				HSSFRow row = sheetAt.getRow(i);
				
				
				 
				 String sName = row.getCell(0).getStringCellValue();
				 
				 String sSex = row.getCell(1).getStringCellValue();
				 
				 String sHobby = row.getCell(2).getStringCellValue();
				 
				 Date sBirth = row.getCell(3).getDateCellValue();
				 
				 String mname = row.getCell(4).getStringCellValue();
			     Integer mId = findById(mname);
				  
				 Stutb stutb = new Stutb();
				 
				 
				 stutb.setsName(sName);
				 stutb.setsSex(sSex);
				 stutb.setsHobby(sHobby);
				 stutb.setsBirth(sBirth);
				 stutb.setmId(mId);
				 stuService.addUser(stutb);
				 
			}
			
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("导入错误",e);
			result.put("errorMsg", "对不起，导入失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	@Autowired
	private majortbMapper majortbMapper;
	
	private Integer findById(String mname) {
		Majortb majortb = new Majortb();
		majortb.setmName(mname);
		Majortb One = majortbMapper.selectOne(majortb);
		
		return One.getmId();
	}


	/**
	 * 备份
	 */
	@RequestMapping("exportStu")
	public void exportStu(HttpServletRequest request,HttpServletResponse response){
		JSONObject result = new JSONObject();
		try {
			String time = TimeUtil.formatTime(new Date(), "yyyyMMddHHmmss");
		    String excelName = "手动备份"+time;
		    StuVo stuVo = new StuVo();
			List<StuVo> list = stuService.findPage(stuVo);
			String[] handers = {"学生编号","学生姓名","学生性别","学生爱好","学生生日","学生专业"};
			// 1导入硬盘
			ExportExcelToDisk(request,handers,list, excelName);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("", "对不起，备份失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
	
	// 导出到硬盘
	@SuppressWarnings("resource")
	private void ExportExcelToDisk(HttpServletRequest request,
			String[] handers, List<StuVo> list, String excleName) throws Exception {
		
		try {
			HSSFWorkbook wb = new HSSFWorkbook();//创建工作簿
			HSSFSheet sheet = wb.createSheet("操作记录备份");//第一个sheet
			HSSFRow rowFirst = sheet.createRow(0);//第一个sheet第一行为标题
			rowFirst.setHeight((short) 500);
			for (int i = 0; i < handers.length; i++) {
				sheet.setColumnWidth((short) i, (short) 4000);// 设置列宽
			}
			//写标题了
			for (int i = 0; i < handers.length; i++) {
			    //获取第一行的每一个单元格
			    HSSFCell cell = rowFirst.createCell(i);
			    //往单元格里面写入值
			    cell.setCellValue(handers[i]);
			}
			for (int i = 0;i < list.size(); i++) {
			    //获取list里面存在是数据集对象
				StuVo stuVo = list.get(i);
			    //创建数据行
			    HSSFRow row = sheet.createRow(i+1);
			    //设置对应单元格的值
			    row.setHeight((short)400);   // 设置每行的高度
			    //"序号","操作人","IP地址","操作时间","操作模块","操作类型","详情"
			    row.createCell(0).setCellValue(i+1);
			    row.createCell(1).setCellValue(stuVo.getsName());
			    row.createCell(2).setCellValue(stuVo.getsSex());
			    row.createCell(3).setCellValue(stuVo.getsHobby());
			    row.createCell(4).setCellValue(TimeUtil.formatTime(stuVo.getsBirth(),"yyyy-MM-dd"));
			    row.createCell(5).setCellValue(stuVo.getMname());
			}
			//写出文件（path为文件路径含文件名）
				OutputStream os;
				File file = new File("d:/img/"+excleName+".xls");
				
				if (!file.exists()){//若此目录不存在，则创建之  
					file.createNewFile();  
					logger.debug("创建文件夹路径为："+ file.getPath());  
	            } 
				os = new FileOutputStream(file);
				wb.write(os);
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
	}
	
}
