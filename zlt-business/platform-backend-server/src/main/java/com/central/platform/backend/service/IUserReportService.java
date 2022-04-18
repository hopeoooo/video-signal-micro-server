package com.central.platform.backend.service;

import com.central.platform.backend.model.UserReport;
import com.central.platform.backend.model.vo.UserReportVo;

import java.util.List;
import java.util.Map;

public interface IUserReportService{

    int saveUserReport(UserReport userReport);

    List<UserReportVo> findUserReportVos(Map<String, Object> params);

    Long findUserReportVoCount(Map<String, Object> params);
}
