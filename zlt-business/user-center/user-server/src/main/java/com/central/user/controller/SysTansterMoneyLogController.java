package com.central.user.controller;

import com.central.common.model.PageResult;
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
    public PageResult<SysTansterMoneyLogVo> findSysTansterMoneyList(@ModelAttribute SysTansterMoneyPageCo params) {
        return sysTansterMoneyLogService.findSysTansterMoneyList(params);
    }
}
