package com.central.platform.backend.controller;

import com.central.common.annotation.LoginUser;
import com.central.common.dto.I18nSourceDTO;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.translate.model.co.QueryI18nInfoPageCo;
import com.central.translate.model.co.SaveI18nInfoCo;
import com.central.translate.model.co.UpdateI18nInfoCo;
import com.central.common.vo.I18nInfoPageVO;
import com.central.common.vo.LanguageLabelVO;
import com.central.translate.feign.TranslateService;
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
@RequestMapping("/other")
public class TranslateController {

    @Autowired
    private TranslateService i18nInfosService;

    /**
     * 更新后台国际化字典
     *
     * @param param   更新参数
     * @return {@link Result} 操作结果
     * @author lance
     * @since 2022 -01-25 14:13:51
     */
    @PostMapping("/translate/backendUpdate")
    @ApiOperation(value = "更新后台国际化字典")
    public Result<String> backendUpdate(
            @Validated(SaveI18nInfoCo.Update.class) @RequestBody UpdateI18nInfoCo param){
        return i18nInfosService.backendUpdate(param);
    }


    /**
     * 更新前台国际化字典
     *
     * @param param   更新参数
     * @return {@link Result} 操作结果
     * @author lance
     * @since 2022 -01-28 12:46:24
     */
    @PostMapping("/translate/frontUpdate")
    @ApiOperation(value = "更新前台国际化字典")
    public Result<String> frontUpdate(
            @Validated(SaveI18nInfoCo.Update.class) @RequestBody UpdateI18nInfoCo param){
        return i18nInfosService.frontUpdate(param);
    }


    /**
     * 新增后台国际化字典
     *
     * @param param   更新参数
     * @return {@link Result} 操作结果
     * @author lance
     * @since 2022 -01-25 14:13:51
     */
    @PostMapping("/translate/backendSave")
    @ApiOperation(value = "更新后台国际化字典")
    public Result<String> backendSave(
            @Validated(SaveI18nInfoCo.Save.class) @RequestBody SaveI18nInfoCo param){
        return i18nInfosService.backendSave(param);
    }


    /**
     * 新增前台国际化字典
     *
     * @param param   更新参数
     * @return {@link Result} 操作结果
     * @author lance
     * @since 2022 -01-28 12:46:24
     */
    @PostMapping("/translate/frontUpdate")
    @ApiOperation(value = "更新前台国际化字典")
    public Result<String> frontSave(
            @Validated(SaveI18nInfoCo.Save.class) @RequestBody SaveI18nInfoCo param){

        return i18nInfosService.frontSave(param);
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
    public Result<PageResult<I18nInfoPageVO>> infos(@ModelAttribute QueryI18nInfoPageCo param){
        return i18nInfosService.infos(param);
    }

    /**
     * 获取所有的后台国际化资源
     *
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -01-25 14:34:28
     */
    @GetMapping("/translate/backendFullSource")
    @ApiOperation(value = "获取所有的后台国际化资源")
    public Result<I18nSourceDTO> backendFullSource() {
        return i18nInfosService.backendFullSource();
    }

    /**
     * 获取所有的前台国际化资源
     *
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -01-28 13:17:46
     */
    @GetMapping("/translate/frontFullSource")
    @ApiOperation(value = "获取所有的前台台国际化资源")
    public Result<I18nSourceDTO> frontFullSource(){
        return i18nInfosService.frontFullSource();
    }


    /**
     * 获取语言标签
     *
     * @return {@link List} 出参释义
     * @author lance
     * @since 2022 -01-28 13:44:41
     */
    @GetMapping("/translate/languageLabel")
    @ApiOperation(value = "获取语言标签")
    public Result<List<LanguageLabelVO>> languageLabel(){
        return Result.succeed(i18nInfosService.languageLabel());
    }

}
