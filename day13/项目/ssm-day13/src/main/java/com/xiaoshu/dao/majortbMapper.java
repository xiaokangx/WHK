package com.xiaoshu.dao;

import com.xiaoshu.base.dao.BaseMapper;
import com.xiaoshu.entity.Majortb;
import com.xiaoshu.entity.MajortbExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface majortbMapper extends BaseMapper<Majortb> {
    long countByExample(MajortbExample example);

    int deleteByExample(MajortbExample example);

    List<Majortb> selectByExample(MajortbExample example);

    int updateByExampleSelective(@Param("record") Majortb record, @Param("example") MajortbExample example);

    int updateByExample(@Param("record") Majortb record, @Param("example") MajortbExample example);
}