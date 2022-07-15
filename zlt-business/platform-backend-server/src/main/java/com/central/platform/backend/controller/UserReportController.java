package com.central.platform.backend.controller;

import cn.hutool.core.collection.CollUtil;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.game.dto.UserGameReportDto;
import com.central.game.feign.GameService;
import com.central.platform.backend.model.co.UserReportCo;
import com.central.platform.backend.model.vo.UserReportVo;
import com.central.platform.backend.service.IUserReportService;
import com.central.user.feign.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/userReport")
@Api(tags = "会员报表")
public class UserReportController {

    @Resource
    private IUserReportService iUserReportService;

    @Resource
    private UserService userService;

    @Resource
    private GameService gameService;

    @ApiOperation(value = "会员报表分页查询")
    @GetMapping("/findPage")
    public Result<PageResult<UserReportVo>> findPage(@ModelAttribute UserReportCo params){
        PageResult<UserReportVo> pageResult = new PageResult();
        Map<String,Object> map = new HashMap<>();
        map.put("startTime",params.getStartTime());
        map.put("endTime",params.getEndTime());
        if (StringUtils.hasText(params.getUsername())){
            SysUser sysUser = userService.selectByUsername(params.getUsername());
            if (sysUser == null){
                pageResult.setData(new ArrayList<>());
                pageResult.setCount(0L);
                return Result.succeed(pageResult);
            }
            map.put("userId",sysUser.getId());
        }
        int page = (params.getPageCode()-1)*params.getPageSize();
        map.put("page",page);
        map.put("pageSize",params.getPageSize());
        List<UserReportVo> userReportVos = iUserReportService.findUserReportVos(map);
        if (userReportVos == null || userReportVos.isEmpty()){
            pageResult.setData(new ArrayList<>());
            pageResult.setCount(0L);
            return Result.succeed(pageResult);
        }
        List<Long> ids = userReportVos.stream().map(UserReportVo::getUserId).collect(Collectors.toList());
        List<SysUser> listByIds = userService.findListByIds(ids).getDatas();
        List<UserReportVo> list = new LinkedList<>();
        if (listByIds != null && !listByIds.isEmpty()){
            userReportVos.forEach(userReportVo -> {
                listByIds.forEach(sysUser -> {
                    if (sysUser.getId().longValue() == userReportVo.getUserId()){
                        userReportVo.setUsername(sysUser.getUsername());
                    }
                });
                list.add(userReportVo);
            });
            pageResult.setData(list);
        }else {
            pageResult.setData(userReportVos);
        }

        pageResult.setCount(iUserReportService.findUserReportVoCount(map));
        return Result.succeed(pageResult);
    }

    @ApiOperation(value = "查看详情")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userId", value = "会员id", required = true),
    })
    @GetMapping("/findUserGameReportDto")
    public Result<List<UserGameReportDto>> findUserGameReportDto(@RequestParam("userId") Long userId){
        Result<List<UserGameReportDto>> userGameReportDto = gameService.findUserGameReportDto(userId);
        if (userGameReportDto.getResp_code() == 0 && CollUtil.isNotEmpty(userGameReportDto.getDatas())){
            List<UserGameReportDto> userGameReportDtos = userGameReportDto.getDatas();
            if (userGameReportDtos != null && !userGameReportDtos.isEmpty()){
                Integer sum = userGameReportDtos.stream().mapToInt(UserGameReportDto::getBetNum).sum();
                userGameReportDtos.forEach(userGameReportDto1 -> {
                    userGameReportDto1.setBetNumRatio(new BigDecimal(userGameReportDto1.getBetNum()/sum));
                    userGameReportDto1.setProfitRatio(userGameReportDto1.getWinLoss().divide(userGameReportDto1.getValidAmount(),4, RoundingMode.HALF_UP));
                });
            }
            return Result.succeed(userGameReportDtos);
        }
        return Result.succeed(new ArrayList<>());
    }
}
