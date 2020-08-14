package com.xiaoshu.dao;

import com.xiaoshu.base.dao.BaseMapper;
import com.xiaoshu.entity.StuVo;
import com.xiaoshu.entity.Stutb;
import com.xiaoshu.entity.StutbExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface stutbMapper extends BaseMapper<Stutb> {
    long countByExample(StutbExample example);

    int deleteByExample(StutbExample example);

    List<Stutb> selectByExample(StutbExample example);

    int updateByExampleSelective(@Param("record") Stutb record, @Param("example") StutbExample example);

    int updateByExample(@Param("record") Stutb record, @Param("example") StutbExample example);

	List<StuVo> findPage(StuVo stuVo);

	List<StuVo> findEcharts(StuVo stuVo);
}