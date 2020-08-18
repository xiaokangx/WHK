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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.dao.JiyunBankMapper;
import com.xiaoshu.entity.JiyunBank;
import com.xiaoshu.entity.JiyunPerson;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.PersonVo;
import com.xiaoshu.entity.Role;
import com.xiaoshu.entity.User;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.PersonService;
import com.xiaoshu.service.RoleService;
import com.xiaoshu.service.UserService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.WriterUtil;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("person")
public class PersonController extends LogController{
	static Logger logger = Logger.getLogger(PersonController.class);

	@Autowired
	private PersonService personService;
	
	@Autowired
	private RoleService roleService ;
	
	@Autowired
	private OperationService operationService;
	
	@Autowired
	RedisTemplate redisTemplate;
	
	@RequestMapping("personIndex")
	public String index(HttpServletRequest request,Integer menuid) throws Exception{
		List<Role> roleList = roleService.findRole(new Role());
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		
		List<JiyunBank> bList = (List<JiyunBank>) redisTemplate.boundHashOps("blist").get("jiyunBank");
		if(bList==null){
			bList = personService.findJiyunBank();
			redisTemplate.boundHashOps("bList").put("jiyunBank", bList);
		}else {
			System.out.println("--------从redis中查询-------");
		}
		
		request.setAttribute("bList", bList);
		
		request.setAttribute("operationList", operationList);
		request.setAttribute("roleList", roleList);
		return "person";
	}
	
	
	@RequestMapping(value="personList",method=RequestMethod.POST)
	public void personList(PersonVo personVo ,HttpServletRequest request,HttpServletResponse response,String offset,String limit) throws Exception{
		try {
			
			Integer pageSize = StringUtil.isEmpty(limit)?ConfigUtil.getPageSize():Integer.parseInt(limit);
			Integer pageNum =  (Integer.parseInt(offset)/pageSize)+1;
			PageInfo<PersonVo> userList= personService.findUserPage(personVo,pageNum,pageSize);
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
	@RequestMapping("reservePerson")
	public void reserveUser(JiyunPerson jiyunPerson,HttpServletRequest request,HttpServletResponse response){
		Integer userId = jiyunPerson.getpId();
		JSONObject result=new JSONObject();
		try {
			if (userId != null) {   // userId不为空 说明是修改
				JiyunPerson userName = personService.existUserWithUserName(jiyunPerson.getpName());
				if(userName != null && userName.getpId().compareTo(userId)==0){
					jiyunPerson.setpId(userId);
					personService.updateUser(jiyunPerson);
					result.put("success", true);
				}else{
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}
				
			}else {   // 添加
				if(personService.existUserWithUserName(jiyunPerson.getpName())==null){  // 没有重复可以添加
					personService.addUser(jiyunPerson);
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
	
	// 添加银行
	@RequestMapping("addBank")
	public void addBank(JiyunBank jiyunBank,HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			
			personService.addBank(jiyunBank);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存用户信息错误",e);
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	@RequestMapping("echartsPerson")
	public void echartsPerson(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			
			List<PersonVo> klist = personService.findEcharts();
			
			
			result.put("klist", klist);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除用户信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	@RequestMapping("deletePerson")
	public void deletePerson(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");
			for (String id : ids) {
				personService.deleteUser(Integer.parseInt(id));
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
	
	@RequestMapping("importPerson")
	public void importPerson(MultipartFile imp,HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			HSSFWorkbook hssfWorkbook = new HSSFWorkbook(imp.getInputStream());
			
			HSSFSheet sheetAt = hssfWorkbook.getSheetAt(0);
			
			int rowNum = sheetAt.getLastRowNum();
			for (int i = 1; i <=rowNum; i++) {
				HSSFRow row = sheetAt.getRow(i);
				String pname = row.getCell(0).getStringCellValue();
				
				String psex = row.getCell(1).getStringCellValue();
				
				String pLike = row.getCell(2).getStringCellValue();
				
				Double numericCellValue = row.getCell(3).getNumericCellValue();
				int pCount = numericCellValue.intValue();
				
				Double numericCellValue2 = row.getCell(4).getNumericCellValue();
				int page = numericCellValue2.intValue();
				
				Date createtime = row.getCell(5).getDateCellValue();
				
				String bname = row.getCell(6).getStringCellValue();
				
				Integer bid = findById(bname);
				
				JiyunPerson person = new JiyunPerson();
				person.setbId(bid);
				person.setCreatetime(createtime);
				person.setpAge(page);
				person.setpCount(pCount);
                person.setpLike(pLike);
                person.setpName(pname);
                person.setpSex(psex);
                personService.addUser(person);
				
			}
			
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除用户信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}

         @Autowired
         private JiyunBankMapper bankMapper;
	private Integer findById(String bname) {
		JiyunBank jiyunBank = new JiyunBank();
		jiyunBank.setbName(bname);
		JiyunBank one = bankMapper.selectOne(jiyunBank);
		
		
		return one.getbId();
	}

    


	
}
