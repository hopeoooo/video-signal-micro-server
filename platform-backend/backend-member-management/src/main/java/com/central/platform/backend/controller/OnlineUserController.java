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

import java.util.List;

/**
 * 在线会员报表
 */
@Slf4j
@RestController
@Api(tags = "在线会员报表api")
@RequestMapping("/platform/user")
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
    @GetMapping("/online/list")
    public Result<List<OnlineUser>> list(@RequestParam("tag") Integer tag) {
        return onlineUserService.maps(tag);
    }

    /**
     * 及时在线会员
     */
    @ApiOperation(value = "及时在线会员")
    @GetMapping("/online/queryPlayerNums")
    public Result<Integer> queryPlayerNums() {
        return onlineUserService.queryPlayerNums();
    }

    /**
     * 会员报表查询
     */
    @ApiOperation(value = "会员报表查询")
    /*@ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "startDate", value = "注册起始时间查询", required = true),
            @ApiImplicitParam(name = "endDate", value = "注册结束时间查询", required = true),
    })
    @GetMapping("/online/findPageList")
    public Result<PageResult<OnlineUser>> findPageList(@RequestParam Map<String, Object> params) {
        PageResult<OnlineUser> pageList = iOnlineUserService.findPageList(params);
        return Result.succeed(pageList);
    }*/

    @GetMapping("/online/findPageList")
    public Result<PageResult<OnlineUser>> findPageList(@ModelAttribute OnlineUserCo params){
        return onlineUserService.findPageList(params);
    }
}
