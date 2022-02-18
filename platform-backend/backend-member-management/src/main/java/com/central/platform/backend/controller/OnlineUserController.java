package com.central.platform.backend.controller;

import com.central.common.model.OnlineUser;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.user.model.co.OnlineUserCo;
import com.central.user.feign.OnlineUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 在线会员报表
 */
@Slf4j
@RestController
@Api(tags = "在线会员报表api")
@RequestMapping("/member")
public class OnlineUserController {

    @Autowired
    private OnlineUserService onlineUserService;

    /**
     * 在线会员报表走势图查询
     */
    @ApiOperation(value = "在线会员报表走势图查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tag", value = "tag:0 当日 tag:1 当月", required = true, dataType = "Integer"),
    })
    @GetMapping("/user/online/list")
    public Result<List<OnlineUser>> list(@RequestParam("tag") Integer tag) {
        return onlineUserService.maps(tag);
    }

    /**
     * 及时在线会员
     */
    @ApiOperation(value = "及时在线会员")
    @GetMapping("/user/online/queryPlayerNums")
    public Result<Integer> queryPlayerNums() {
        return onlineUserService.queryPlayerNums();
    }

    /**
     * 会员报表查询
     */
    @ApiOperation(value = "会员报表查询")
    @GetMapping("/user/online/findPageList")
    public Result<PageResult<OnlineUser>> findPageList(@ModelAttribute OnlineUserCo params){
        return onlineUserService.findPageList(params);
    }
}
