package com.central.platform.backend.controller;

import com.central.common.dto.I18nSourceDTO;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
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
import java.util.List;
import java.util.Objects;

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
    private TranslateService i18nInfosService;

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
    @PostMapping("/frontUpdate")
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
    @PostMapping("/backendSave")
    @ApiOperation(value = "新增后台国际化字典")
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
    @PostMapping("/frontSave")
    @ApiOperation(value = "新增前台国际化字典")
    public Result<String> frontSave(
            @Validated(SaveI18nInfoCo.Save.class) @RequestBody SaveI18nInfoCo param){
        if (Objects.isNull(param.getFromOf())) {
            return Result.failed("参数必传");
        }
        return i18nInfosService.frontSave(param);
    }

    /**
     * 查询后台国际化字典分页
     *
     * @param param 查询参数
     * @return {@link PageResult} 分页数据
     * @author lance
     * @since 2022 -01-25 14:13:51
     */
    @GetMapping("/infos")
    @ApiOperation(value = "查询国际化字典分页")
    public Result<PageResult<I18nInfoPageVO>> infos(@ModelAttribute QueryI18nInfoPageCo param){
        return i18nInfosService.infos(param);
    }

    /**
     * 查询前台国际化字典分页
     *
     * @param param 查询参数
     * @return {@link PageResult} 分页数据
     * @author lance
     * @since 2022 -01-25 14:13:51
     */
    @GetMapping("/frontInfos")
    @ApiOperation(value = "查询前台国际化字典分页")
    public Result<PageResult<I18nInfoPageVO>> frontInfos(@ModelAttribute QueryI18nInfoPageCo param){
        return i18nInfosService.infos(param);
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
        return i18nInfosService.backendFullSource();
    }

//    /**
//     * 获取所有的前台PC国际化资源
//     *
//     * @return {@link Result} 出参释义
//     * @author lance
//     * @since 2022 -01-28 13:17:46
//     */
//    @GetMapping("/frontFullSource")
//    @ApiOperation(value = "获取所有的前台PC国际化资源")
//    public Result<I18nSourceDTO> frontFullSource(){
//        return i18nInfosService.frontFullSource();
//    }
//
//    /**
//     * 获取所有的前台App国际化资源
//     *
//     * @return {@link Result} 出参释义
//     * @author lance
//     * @since 2022 -01-28 13:17:46
//     */
//    @GetMapping("/frontAppFullSource")
//    @ApiOperation(value = "获取所有的前台App国际化资源")
//    public Result<I18nSourceDTO> frontAppFullSource(){
//        return i18nInfosService.frontAppFullSource();
//    }
//
//    /**
//     * 获取所有的前台Message国际化资源
//     *
//     * @return {@link Result} 出参释义
//     * @author lance
//     * @since 2022 -01-28 13:17:46
//     */
//    @GetMapping("/frontMessageFullSource")
//    @ApiOperation(value = "获取所有的前台Message国际化资源")
//    public Result<I18nSourceDTO> frontMessageFullSource(){
//        return i18nInfosService.frontMessageFullSource();
//    }

    /**
     * 获取语言标签
     *
     * @return {@link List} 出参释义
     * @author lance
     * @since 2022 -01-28 13:44:41
     */
    @GetMapping("/languageLabel")
    @ApiOperation(value = "获取语言标签")
    public Result<List<LanguageLabelVO>> languageLabel(){
        return Result.succeed(i18nInfosService.languageLabel());
    }

}
