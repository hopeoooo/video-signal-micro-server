package com.central.user.controller;

import com.central.common.annotation.LoginUser;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.vo.SysTansterMoneyLogVo;
import com.central.user.dto.UserTansterMoneyDto;
import com.central.user.model.co.SysTansterMoneyPageCo;
import com.central.user.service.ISysTansterMoneyLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 会员账变记录
 */
@Slf4j
@RestController
@Api(tags = "账变记录")
@RequestMapping("/sysTansterMoney")
public class SysTansterMoneyLogController {

    @Autowired
    private ISysTansterMoneyLogService sysTansterMoneyLogService;

    @ApiOperation(value = "根据类型查询账变记录")
    @GetMapping("/findOrderTypeAccountChangeList")
    public Result<List<SysTansterMoneyLogVo>> findOrderTypeAccountChangeList(@RequestParam(value ="orderType" ,required = false)String orderType,
                                                                             @RequestParam(value ="listId", required = false)List<Long> listId) {
        List<SysTansterMoneyLogVo> list = sysTansterMoneyLogService.findOrderTypeAccountChangeList(orderType,listId);
        return Result.succeed(list);
    }
    /**
     * 用户查询
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "用户账变列表")
    @GetMapping("/findList")
    public PageResult<SysTansterMoneyLogVo> findSysTansterMoneyList(@Valid @ModelAttribute SysTansterMoneyPageCo params) {
        return sysTansterMoneyLogService.findSysTansterMoneyList(params);
    }

    @ApiOperation(value = "根据父级查询转账记录")
    @GetMapping("/findAllByParent")
    public Result<List<SysTansterMoneyLogVo>> findAllByParent(@ModelAttribute SysTansterMoneyPageCo params) {
        List<SysTansterMoneyLogVo> list = sysTansterMoneyLogService.findAllByParent(params);
        return Result.succeed(list);
    }

    @ApiOperation(value = "用户账变列表-前台")
    @GetMapping("/findAmountChangeList")
    public Result<PageResult<SysTansterMoneyLogVo>> findAmountChangeList(@Valid @ModelAttribute SysTansterMoneyPageCo params, @LoginUser SysUser user) {
        params.setUserId(user.getId());
        PageResult<SysTansterMoneyLogVo> amountChangeList = sysTansterMoneyLogService.findAmountChangeList(params);
        return Result.succeed(amountChangeList)
    }

    @ResponseBody
    @ApiOperation(value = "会员充提报表")
    @GetMapping("/findUserTansterMoneyDto")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "startTime", value = "开始时间", required = false),
        @ApiImplicitParam(name = "endTime", value = "结束时间", required = false),
        @ApiImplicitParam(name = "orderType", value = "账变类型", required = false),
    })
    public Result<List<UserTansterMoneyDto>> findUserTansterMoneyDto(@RequestParam Map<String, Object> params) {
        List<UserTansterMoneyDto> userReportDtos = sysTansterMoneyLogService.findUserTansterMoneyDto(params);
        return Result.succeed(userReportDtos);
    }
}
