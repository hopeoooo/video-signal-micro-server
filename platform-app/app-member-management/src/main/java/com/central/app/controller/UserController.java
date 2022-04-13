package com.central.app.controller;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.user.feign.UserService;
import com.central.user.model.vo.RoomFollowVo;
import com.central.user.model.vo.SysUserMoneyVo;
import com.central.user.model.vo.UserInfoVo;
import com.central.user.model.vo.WashCodeChangeVo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@Api(tags = "会员相关接口")
@Slf4j
@RequestMapping("/member")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/users/updateHeadImg")
    public Result<String> updateHeadImgUrl(@NotBlank(message = "headImg不允许为空") String headImg){
        return userService.updateHeadImgUrl(headImg);
    }

    @GetMapping("/users/info")
    public Result<UserInfoVo> findUserInfoById(){
        return userService.findUserInfoById();
    }

    @GetMapping("/userMoney/getMoney")
    public Result<SysUserMoneyVo> getMoney(){
        return userService.getMoney();
    }

    @GetMapping("/userMoney/receiveWashCode")
    public Result<String> receiveWashCode(){
        return userService.receiveWashCode();
    }

//    @GetMapping("/washCodeChange/getWashCodeRecord")
//    public Result<PageResult<WashCodeChangeVo>> getWashCodeRecord(String date){
//        return userService.getWashCodeRecord(date);
//    }

    @GetMapping("/followList/getRoomFollowList")
    public Result<List<RoomFollowVo>> getRoomFollowList(){
        return userService.getRoomFollowList();
    }

    @PostMapping("/followList/addOrRemoveFollow/{roomId}")
    public Result addFollow(@PathVariable("roomId") Long roomId){
        return userService.addFollow(roomId);
    }

}
