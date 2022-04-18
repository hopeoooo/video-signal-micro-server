package com.central.platform.backend.controller;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.platform.backend.model.co.UserReportCo;
import com.central.platform.backend.model.vo.UserReportVo;
import com.central.platform.backend.service.IUserReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/userReport")
@Api(tags = "会员报表")
public class UserReportController {

    @Resource
    private IUserReportService iUserReportService;

    @ApiOperation(value = "会员报表分页查询")
    @GetMapping("/findPage")
    public Result<PageResult<UserReportVo>> findPage(@ModelAttribute UserReportCo params){
        Map<String,Object> map = new HashMap<>();
        map.put("startTime",params.getStartTime());
        map.put("endTime",params.getEndTime());
        if (StringUtils.hasText(params.getUsername())){
            map.put("userId",params.getUsername());
        }
        int page = (params.getPageCode()-1)*params.getPageSize();
        map.put("page",page);
        map.put("pageSize",params.getPageSize());
        List<UserReportVo> userReportVos = iUserReportService.findUserReportVos(map);
        PageResult<UserReportVo> pageResult = new PageResult();
        pageResult.setData(userReportVos);
        pageResult.setCount(iUserReportService.findUserReportVoCount(map));
        return Result.succeed(pageResult);
    }
}
