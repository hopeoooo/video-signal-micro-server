package com.central.translate.controller;

import com.central.common.annotation.LoginUser;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.params.translate.QueryI18nInfoPageParam;
import com.central.common.params.translate.UpdateI18nInfoParam;
import com.central.common.vo.I18nInfoPageVO;
import com.central.translate.service.I18nInfosService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 翻译模块
 *
 * @author lance
 * @since 2022 -01-25 14:13:51
 */
@Slf4j
@RestController
@Api(tags = "翻译api")
public class TranslateController {

    @Autowired
    private I18nInfosService i18nInfosService;

    /**
     * 更新国际化字典
     *
     * @param sysUser 登录用户
     * @param param   更新参数
     * @return {@link Result} 操作结果
     * @author lance
     * @since 2022 -01-25 14:13:51
     */
    @PostMapping("/translate/update")
    @ApiOperation(value = "更新国际化字典")
    public Result<String> update(
            @LoginUser SysUser sysUser,
            @RequestBody UpdateI18nInfoParam param){
        try {
            i18nInfosService.updateI18nInfo(sysUser.getUsername(), param);
            return Result.succeed("操作成功");
        } catch (Exception ex) {
            return Result.failed("操作失败");
        }
    }

    /**
     * 查询国际化字典分页
     *
     * @param param 查询参数
     * @return {@link PageResult} 分页数据
     * @author lance
     * @since 2022 -01-25 14:13:51
     */
    @GetMapping("/translate/infos")
    @ApiOperation(value = "查询国际化字典分页")
    public PageResult<I18nInfoPageVO> infos(@ModelAttribute QueryI18nInfoPageParam param){
        return i18nInfosService.findInfos(param);
    }

}
