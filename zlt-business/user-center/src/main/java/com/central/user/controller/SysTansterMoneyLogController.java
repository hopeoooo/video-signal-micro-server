package com.central.user.controller;

import com.central.common.model.PageResult;
import com.central.common.model.SysUser;
import com.central.common.vo.SysTansterMoneyLogVo;
import com.central.user.service.ISysTansterMoneyLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer")
    })
    @GetMapping("/findList")
    public PageResult<SysTansterMoneyLogVo> findSysTansterMoneyList(@RequestParam Map<String, Object> params) {
        return sysTansterMoneyLogService.findSysTansterMoneyList(params);
    }
}
