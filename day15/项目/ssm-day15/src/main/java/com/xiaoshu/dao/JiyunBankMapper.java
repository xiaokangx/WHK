package com.xiaoshu.dao;

import com.xiaoshu.base.dao.BaseMapper;
import com.xiaoshu.entity.JiyunBank;
import com.xiaoshu.entity.JiyunBankExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface JiyunBankMapper extends BaseMapper<JiyunBank> {
    long countByExample(JiyunBankExample example);

    int deleteByExample(JiyunBankExample example);

    List<JiyunBank> selectByExample(JiyunBankExample example);

    int updateByExampleSelective(@Param("record") JiyunBank record, @Param("example") JiyunBankExample example);

    int updateByExample(@Param("record") JiyunBank record, @Param("example") JiyunBankExample example);
}