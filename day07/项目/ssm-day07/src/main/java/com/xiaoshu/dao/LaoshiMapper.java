package com.xiaoshu.dao;

import com.xiaoshu.base.dao.BaseMapper;
import com.xiaoshu.entity.Laoshi;
import com.xiaoshu.entity.LaoshiExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface LaoshiMapper extends BaseMapper<Laoshi> {
    long countByExample(LaoshiExample example);

    int deleteByExample(LaoshiExample example);

    List<Laoshi> selectByExample(LaoshiExample example);

    int updateByExampleSelective(@Param("record") Laoshi record, @Param("example") LaoshiExample example);

    int updateByExample(@Param("record") Laoshi record, @Param("example") LaoshiExample example);
}