package com.central.translate.controller;

import com.central.common.annotation.LoginUser;
import com.central.common.constant.I18nKeys;
import com.central.common.dto.I18nSourceDTO;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.translate.model.co.QueryI18nInfoPageCo;
import com.central.translate.model.co.SaveI18nInfoCo;
import com.central.translate.model.co.UpdateI18nInfoCo;
import com.central.common.vo.I18nInfoPageVO;
import com.central.common.vo.LanguageLabelVO;
import com.central.translate.service.I18nInfosService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

/**
 * 翻译模块
 *
 * @author lance
 * @since 2022 -01-25 14:13:51
 */
@Slf4j
@RestController
@Api(tags = "翻译api")
@RequestMapping("/translate")
public class TranslateController {

    @Autowired
    private I18nInfosService i18nInfosService;

    /**
     * 更新后台国际化字典
     *
     * @param param   更新参数
     * @return {@link Result} 操作结果
     * @author lance
     * @since 2022 -01-25 14:13:51
     */
    @PostMapping("/backendUpdate")
    @ApiOperation(value = "更新后台国际化字典")
    public Result<String> backendUpdate(
            @ApiIgnore @LoginUser SysUser sysUser,
            @RequestBody @Validated(SaveI18nInfoCo.Update.class) UpdateI18nInfoCo param){
        param.setOperator(sysUser.getUsername());
        i18nInfosService.updateI18nInfo(I18nKeys.BACKEND, param);
        return Result.succeed("操作成功");
    }


    /**
     * 更新前台国际化字典
     *
     * @param param   更新参数
     * @return {@link Result} 操作结果
     * @author lance
     * @since 2022 -01-28 12:46:24
     */
    @PostMapping("/frontUpdate")
    @ApiOperation(value = "更新前台国际化字典")
    public Result<String> frontUpdate(
            @ApiIgnore @LoginUser SysUser sysUser,
            @RequestBody @Validated(SaveI18nInfoCo.Update.class) UpdateI18nInfoCo param){
        param.setOperator(sysUser.getUsername());
        i18nInfosService.updateI18nInfo(I18nKeys.FRONT, param);
        return Result.succeed("操作成功");
    }

    /**
     * 新增后台国际化字典
     *
     * @param param   更新参数
     * @return {@link Result} 操作结果
     * @author lance
     * @since 2022 -01-25 14:13:51
     */
    @PostMapping("/backendSave")
    @ApiOperation(value = "更新后台国际化字典")
    public Result<String> backendSave(
            @ApiIgnore @LoginUser SysUser sysUser,
            @RequestBody @Validated(SaveI18nInfoCo.Save.class) SaveI18nInfoCo param){
        param.setOperator(sysUser.getUsername());
        i18nInfosService.saveI18nInfo(I18nKeys.BACKEND, param);
        return Result.succeed("操作成功");
    }


    /**
     * 新增前台国际化字典
     *
     * @param param   更新参数
     * @return {@link Result} 操作结果
     * @author lance
     * @since 2022 -01-28 12:46:24
     */
    @PostMapping("/frontSave")
    @ApiOperation(value = "更新前台国际化字典")
    public Result<String> frontSave(
            @ApiIgnore @LoginUser SysUser sysUser,
            @RequestBody @Validated(SaveI18nInfoCo.Save.class) SaveI18nInfoCo param){
        param.setOperator(sysUser.getUsername());
        i18nInfosService.saveI18nInfo(I18nKeys.FRONT, param);
        return Result.succeed("操作成功");
    }

    /**
     * 查询国际化字典分页
     *
     * @param param 查询参数
     * @return {@link PageResult} 分页数据
     * @author lance
     * @since 2022 -01-25 14:13:51
     */
    @GetMapping("/infos")
    @ApiOperation(value = "查询国际化字典分页")
    public Result<PageResult<I18nInfoPageVO>> infos(@Valid @ModelAttribute QueryI18nInfoPageCo param){
        return Result.succeed(i18nInfosService.findInfos(param));
    }

    /**
     * 获取所有的后台国际化资源
     *
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -01-25 14:34:28
     */
    @GetMapping("/backendFullSource")
    @ApiOperation(value = "获取所有的后台国际化资源")
    public Result<I18nSourceDTO> backendFullSource() {
        return Result.succeed(i18nInfosService.getBackendFullI18nSource());
    }

    /**
     * 获取所有的前台国际化资源
     *
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -01-28 13:17:46
     */
    @GetMapping("/frontFullSource")
    @ApiOperation(value = "获取所有的前台台国际化资源")
    public Result<I18nSourceDTO> frontFullSource(){
        return Result.succeed(i18nInfosService.getFrontFullI18nSource());
    }


    /**
     * 获取语言标签
     *
     * @return {@link List} 出参释义
     * @author lance
     * @since 2022 -01-28 13:44:41
     */
    @GetMapping("/languageLabel")
    @ApiOperation(value = "获取语言标签")
    public List<LanguageLabelVO> languageLabel(){
        return i18nInfosService.getLanguageLabel();
    }

}
