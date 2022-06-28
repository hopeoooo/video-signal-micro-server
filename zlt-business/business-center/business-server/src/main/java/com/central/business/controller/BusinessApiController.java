package com.central.business.controller;

import com.alibaba.fastjson.JSONObject;
import com.central.business.config.BusinessContextHolder;
import com.central.business.constant.BusinessConstants;
import com.central.business.feign.UaaService;
import com.central.business.model.co.*;
import com.central.business.model.vo.GameRecordVo;
import com.central.business.model.vo.TransferVo;
import com.central.business.properties.BusinessProperties;
import com.central.business.utils.CheckSignatureUtil;
import com.central.common.constant.CommonConstant;
import com.central.common.model.CodeEnum;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.model.SysUserMoney;
import com.central.common.utils.DateUtil;
import com.central.common.vo.SysTansterMoneyLogVo;
import com.central.game.feign.GameService;
import com.central.game.model.GameRecord;
import com.central.game.model.co.GameRecordBetCo;
import com.central.user.feign.UserService;
import com.central.user.model.co.SysTansterMoneyPageCo;
import com.central.user.model.co.SysUserCo;
import com.central.user.model.vo.SysUserMoneyVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private GameService gameService;
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
        if (co.getType() != 8 && co.getType() != 9) {
            return Result.failed("加扣点类型错误");
        }
        if (co.getMoney().compareTo(BigDecimal.ZERO) < 1) {
            return Result.failed("金额必须大于0");
        }
        SysUser sysUser = userService.selectByUsername(co.getUserName());
        if (sysUser == null) {
            return Result.failed("用户不存在");
        }
        Result<SysUserMoney> result = userService.transterMoney(sysUser.getId(), co.getMoney(), null, co.getType(), co.getTraceId(),null, null);
        if (result.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            return result;
        }
        Result<BigDecimal> succeed = Result.succeed(result.getDatas().getMoney());
        return succeed;
    }

    @GetMapping("/getTransferRecord")
    @ApiOperation(value = "查询转账记录")
    public Result<List<TransferVo>> getTransferRecord(@Valid @ModelAttribute TransferCo co) {
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

    @GetMapping("/getSumMoney")
    @ApiOperation(value = "查询所有用户总余额")
    public Result<BigDecimal> getSumMoney() {
        Result<BigDecimal> result = userService.getSumMoneyByParent(BusinessContextHolder.getBusiness());
        return result;
    }

    @GetMapping("/getMoneyByUserName")
    @ApiOperation(value = "查询单个用户余额")
    public Result<BigDecimal> getSumMoney(@Valid @ModelAttribute LoginCo co) {
        if (!CheckSignatureUtil.checkSignature(co)) {
            return Result.failed("参数签名校验失败");
        }
        Result result = userService.getMoneyByUserName(co.getUserName());
        if (result.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            return result;
        }
        SysUserMoneyVo vo = (SysUserMoneyVo) result.getDatas();
        return Result.succeed(vo.getMoney());
    }

    @GetMapping("/getBetRecord")
    @ApiOperation(value = "查询下注记录")
    public Result<List<GameRecordVo>> getBetRecord(@Valid @ModelAttribute GameRecordCo co) {
        if (!CheckSignatureUtil.checkSignature(co)) {
            return Result.failed("参数签名校验失败");
        }
   /*     Result resultDateTime = checkDateTime(co.getStartTime(), co.getEndTime());
        if (resultDateTime.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            return resultDateTime;
        }*/
        GameRecordBetCo params = new GameRecordBetCo();
        BeanUtils.copyProperties(co, params);
        params.setParent(BusinessContextHolder.getBusiness());
        Result result = gameService.getGameRecordByParent(params);
        if (result.getResp_code() != CodeEnum.SUCCESS.getCode()) {
            return result;
        }
        List<GameRecord> datas = (List<GameRecord>) result.getDatas();
        List<GameRecordVo> datasList = datas.stream().map(t -> {
            GameRecordVo vo = new GameRecordVo();
            BeanUtils.copyProperties(t, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.succeed(datasList);
    }

    private Result checkDateTime(String startTime, String endTime) {
        SimpleDateFormat df = DateUtil.getSimpleDateFormat();
        df.setLenient(false);//表示严格验证
        try {
            long startTimeNum = df.parse(startTime).getTime();
            long endTimeNum = df.parse(endTime).getTime();
            if (endTimeNum < startTimeNum) {
                return Result.failed("开始时间不能大于结束时间");
            }
            long diff = new Date().getTime() - startTimeNum;
            //注单数据只允许查询60天。
            long days = diff / (1000 * 60 * 60 * 24);
            if (days > 60) {
                return Result.failed("仅允许查询60天内的数据");
            }
        } catch (ParseException e) {
            return Result.failed("开始时间或结束时间格式填写错误,正确格式为:yyyy-MM-dd HH:mm:ss");
        }
        return Result.succeed();
    }
}
