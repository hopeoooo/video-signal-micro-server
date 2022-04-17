package com.central.platform.backend.service.impl;

import com.central.platform.backend.mapper.UserReportMapper;
import com.central.platform.backend.model.UserReport;
import com.central.platform.backend.model.vo.UserReportVo;
import com.central.platform.backend.service.IUserReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserReportServiceImpl implements IUserReportService{
    @Autowired
    private UserReportMapper userReportMapper;

    @Override
    public int saveUserReport(UserReport userReport) {
        return userReportMapper.saveUserReport(userReport);
    }

    @Override
    public List<UserReportVo> findUserReportVos(Map<String, Object> params) {
        return userReportMapper.findUserReportVos(params);
    }

    @Override
    public Long findUserReportVoCount(Map<String, Object> params) {
        return userReportMapper.findUserReportVoCount(params);
    }
}
