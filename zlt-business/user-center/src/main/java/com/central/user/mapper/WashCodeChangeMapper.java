package com.central.user.mapper;

import com.central.common.model.WashCodeChange;
import com.central.db.mapper.SuperMapper;
import com.central.user.vo.WashCodeChangeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WashCodeChangeMapper extends SuperMapper<WashCodeChange> {

    List<WashCodeChangeVo> getWashCodeRecord(@Param("userId") Long userId);
}
