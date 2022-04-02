package com.central.user.controller;

import com.central.common.annotation.LoginUser;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.vo.SysTansterMoneyLogVo;
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
    public PageResult<SysTansterMoneyLogVo> findAmountChangeList(@Valid @ModelAttribute SysTansterMoneyPageCo params, @LoginUser SysUser user) {
        params.setUserId(user.getId());
        return sysTansterMoneyLogService.findAmountChangeList(params);
    }


}
