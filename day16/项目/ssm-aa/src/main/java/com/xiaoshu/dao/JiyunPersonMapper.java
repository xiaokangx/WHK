package com.xiaoshu.dao;

import com.xiaoshu.base.dao.BaseMapper;
import com.xiaoshu.entity.JiyunPerson;
import com.xiaoshu.entity.JiyunPersonExample;
import com.xiaoshu.entity.PersonVo;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface JiyunPersonMapper extends BaseMapper<JiyunPerson> {
    long countByExample(JiyunPersonExample example);

    int deleteByExample(JiyunPersonExample example);

    List<JiyunPerson> selectByExample(JiyunPersonExample example);

    int updateByExampleSelective(@Param("record") JiyunPerson record, @Param("example") JiyunPersonExample example);

    int updateByExample(@Param("record") JiyunPerson record, @Param("example") JiyunPersonExample example);

	List<PersonVo> findPage(PersonVo personVo);

	List<PersonVo> findEcharts();
}