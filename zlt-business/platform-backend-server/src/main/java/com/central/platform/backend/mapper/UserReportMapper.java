package com.central.platform.backend.mapper;

import com.central.platform.backend.model.UserReport;
import com.central.platform.backend.model.vo.UserReportVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserReportMapper {

    int saveUserReport(@Param("u") UserReport userReport);

    List<UserReportVo> findUserReportVos(@Param("p")Map<String, Object> params);

    Long findUserReportVoCount(@Param("p")Map<String, Object> params);
}
