package com.xiaoshu.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class PersonVo extends JiyunPerson{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String bname;
	
	private Integer num;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date start;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date end;

	public String getBname() {
		return bname;
	}

	public void setBname(String bname) {
		this.bname = bname;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	
	

}
