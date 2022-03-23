package com.central.business.controller;

import com.alibaba.fastjson.JSONObject;
import com.central.business.config.BusinessContextHolder;
import com.central.business.constant.BusinessConstants;
import com.central.business.feign.UaaService;
import com.central.business.model.co.ChangeBalanceCo;
import com.central.business.model.co.LoginCo;
import com.central.business.model.co.RegisterCo;
import com.central.business.model.co.TransferCo;
import com.central.business.model.vo.TransferVo;
import com.central.business.properties.BusinessProperties;
import com.central.business.utils.CheckSignatureUtil;
import com.central.common.constant.CommonConstant;
import com.central.common.model.CodeEnum;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.model.SysUserMoney;
import com.central.common.vo.SysTansterMoneyLogVo;
import com.central.user.feign.UserService;
import com.central.user.model.co.SysTansterMoneyPageCo;
import com.central.user.model.co.SysUserCo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/businessApi")
@Api(tags = "商户接口")
public class BusinessApiController {

    @Autowired
    private UserService userService;
    @Autowired
    private UaaService uaaService;
    @Autowired
    private BusinessProperties businessProperties;


    @PostMapping("/register")
    @ApiOperation(value = "注册新用户")
    public Result register(@Valid @RequestBody RegisterCo co) {
        if (!CheckSignatureUtil.checkSignature(co)) {
            return Result.failed("参数签名校验失败");
        }
        SysUserCo sysUser = new SysUserCo();
        sysUser.setUsername(co.getUserName());
        sysUser.setType(CommonConstant.USER_TYPE_APP);
        sysUser.setEnabled(true);
        sysUser.setDel(false);
        sysUser.setParent(BusinessContextHolder.getBusiness());
        Result result = userService.saveOrUpdate(sysUser);
        result.setDatas(null);
        return result;
    }

    @PostMapping("/login")
    @ApiOperation(value = "登录游戏大厅")
    public Result<String> login(@Valid @RequestBody LoginCo co) {
        if (!CheckSignatureUtil.checkSignature(co)) {
            return Result.failed("参数签名校验失败");
        }
        String deviceId = UUID.randomUUID().toString();
        Result result = uaaService.login(businessProperties.getAuthorization(), co.getUserName(), CommonConstant.DEF_USER_PASSWORD, BusinessConstants.AUTHENTICATION_MODE, deviceId);
        if (result.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            return result;
        }
        String datas = JSONObject.toJSONString(result.getDatas());
        JSONObject jsonObject = JSONObject.parseObject(datas);
        String accessToken = jsonObject.getString("access_token");
        String url = businessProperties.getBaseUrl() + "/#/?token=" + accessToken;
        result.setDatas(url);
        return result;
    }

    @PostMapping("/changeBalance")
    @ApiOperation(value = "加扣点")
    public Result changeBalance(@Valid @RequestBody ChangeBalanceCo co) {
        if (!CheckSignatureUtil.checkSignature(co)) {
            return Result.failed("参数签名校验失败");
        }
        if (co.getType() != 2 && co.getType() != 3) {
            return Result.failed("加扣点类型错误");
        }
        if (co.getMoney().compareTo(BigDecimal.ZERO) < 1) {
            return Result.failed("金额必须大于0");
        }
        SysUser sysUser = userService.selectByUsername(co.getUserName());
        if (sysUser == null) {
            return Result.failed("用户不存在");
        }
        Result<SysUserMoney> result = userService.transterMoney(sysUser.getId(), co.getMoney(), null, co.getType(), co.getTraceId());
        if (result.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            return result;
        }
        Result<BigDecimal> succeed = Result.succeed(result.getDatas().getMoney());
        return succeed;
    }


    @PostMapping("/getTransferRecord")
    @ApiOperation(value = "查询转账记录")
    public Result<List<TransferVo>> getTransferRecord(@Valid @RequestBody TransferCo co) {
        if (!CheckSignatureUtil.checkSignature(co)) {
            return Result.failed("参数签名校验失败");
        }
        SysTansterMoneyPageCo params = new SysTansterMoneyPageCo();
        BeanUtils.copyProperties(co, params);
        params.setParent(BusinessContextHolder.getBusiness());
        Result result = userService.findAllByParent(params);
        if (result.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            return result;
        }
        List<SysTansterMoneyLogVo> datas = (List<SysTansterMoneyLogVo>) result.getDatas();
        List<TransferVo> datasList = datas.stream().map(t -> {
            TransferVo vo = new TransferVo();
            BeanUtils.copyProperties(t, vo);
            return vo;
        }).collect(Collectors.toList());
        result.setDatas(datasList);
        return result;
    }
}
