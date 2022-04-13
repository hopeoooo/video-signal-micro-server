package com.central.game.mapper;

import com.central.game.model.WashCodeChange;
import com.central.db.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WashCodeChangeMapper extends SuperMapper<WashCodeChange> {

    List<WashCodeChange> getWashCodeRecord(@Param("userId") Long userId,@Param("startTime") String startTime,@Param("endTime") String endTime);
}
