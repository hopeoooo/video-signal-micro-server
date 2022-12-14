package com.central.translate.controller;

import com.central.common.annotation.LoginUser;
import com.central.common.constant.I18nKeys;
import com.central.common.dto.I18nSourceDTO;
import com.central.common.model.I18nInfo;
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
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
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
    private I18nInfosService i18nInfosService;


    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除")
    public Result<String> delete(@PathVariable Long id) {
        I18nInfo i18nInfo = i18nInfosService.selectById(id);
        if (Objects.isNull(i18nInfo)){
            return Result.failed("删除失败");
        }
        Boolean b = i18nInfosService.deleteById(id, i18nInfo);
        if (b){
            return Result.succeed("操作成功");
        }else {
            return Result.failed("删除失败");
        }
    }
    /**
     * 更新后台国际化字典
     *
     * @param param 更新参数
     * @return {@link Result} 操作结果
     * @author lance
     * @since 2022 -01-25 14:13:51
     */
    @PostMapping("/backendUpdate")
    @ApiOperation(value = "更新后台国际化字典")
    public Result<String> backendUpdate(@ApiIgnore @LoginUser SysUser sysUser,
        @RequestBody @Validated(SaveI18nInfoCo.Update.class) UpdateI18nInfoCo param) {
        param.setOperator(sysUser.getUsername());
        if (!i18nInfosService.updateI18nInfo(param.getFromOf(), param)){
            return Result.failed("数据重复");
        }
        return Result.succeed("操作成功");
    }

    /**
     * 更新前台国际化字典
     *
     * @param param 更新参数
     * @return {@link Result} 操作结果
     * @author lance
     * @since 2022 -01-28 12:46:24
     */
    @PostMapping("/frontUpdate")
    @ApiOperation(value = "更新前台国际化字典")
    public Result<String> frontUpdate(@ApiIgnore @LoginUser SysUser sysUser,
        @RequestBody @Validated(SaveI18nInfoCo.Update.class) UpdateI18nInfoCo param) {
        param.setOperator(sysUser.getUsername());
        if (Objects.isNull(param.getFromOf())) {
            return Result.failed("参数必传");
        }
        if (!i18nInfosService.updateI18nInfo(param.getFromOf(), param)){
            return Result.failed("数据重复");
        }
        return Result.succeed("操作成功");
    }

    /**
     * 新增后台国际化字典
     *
     * @param param 更新参数
     * @return {@link Result} 操作结果
     * @author lance
     * @since 2022 -01-25 14:13:51
     */
    @PostMapping("/backendSave")
    @ApiOperation(value = "新增后台国际化字典")
    public Result<String> backendSave(@ApiIgnore @LoginUser SysUser sysUser,
        @RequestBody @Validated(SaveI18nInfoCo.Save.class) SaveI18nInfoCo param) {
        param.setOperator(sysUser.getUsername());
        if (!i18nInfosService.saveI18nInfo(param.getFromOf(), param)){
            return Result.failed("数据重复");
        }
        return Result.succeed("操作成功");
    }

    /**
     * 新增前台国际化字典
     *
     * @param param 更新参数
     * @return {@link Result} 操作结果
     * @author lance
     * @since 2022 -01-28 12:46:24
     */
    @PostMapping("/frontSave")
    @ApiOperation(value = "新增前台国际化字典")
    public Result<String> frontSave(@ApiIgnore @LoginUser SysUser sysUser,
        @RequestBody @Validated(SaveI18nInfoCo.Save.class) SaveI18nInfoCo param) {
        param.setOperator(sysUser.getUsername());
        if (Objects.isNull(param.getFromOf())) {
            return Result.failed("参数必传");
        }
        if (!i18nInfosService.saveI18nInfo(param.getFromOf(), param)){
            return Result.failed("数据重复");
        }
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
    public Result<PageResult<I18nInfoPageVO>> infos(@Valid @ModelAttribute QueryI18nInfoPageCo param) {
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
     * 获取所有的前台PC国际化资源
     *
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -01-28 13:17:46
     */
    @GetMapping("/frontFullSource")
    @ApiOperation(value = "获取所有前台PC国际化资源")
    public Result<I18nSourceDTO> frontFullSource() {
        return Result.succeed(i18nInfosService.getFrontFullI18nSource(I18nKeys.FRONT_PC));
    }

    /**
     * 获取所有的前台APP国际化资源
     *
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -01-28 13:17:46
     */
    @GetMapping("/frontAppFullSource")
    @ApiOperation(value = "获取所有前台APP国际化资源")
    public Result<I18nSourceDTO> frontAppFullSource() {
        return Result.succeed(i18nInfosService.getFrontFullI18nSource(I18nKeys.FRONT_APP));
    }

    /**
     * 获取所有的前台message国际化资源
     *
     * @return {@link Result} 出参释义
     * @author lance
     * @since 2022 -01-28 13:17:46
     */
    @GetMapping("/frontMessageFullSource")
    @ApiOperation(value = "获取所有的前台台国际化资源")
    public Result<I18nSourceDTO> frontMessageFullSource() {
        return Result.succeed(i18nInfosService.getFrontFullI18nSource(I18nKeys.FRONT_MESSAGE));
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
    public List<LanguageLabelVO> languageLabel() {
        return i18nInfosService.getLanguageLabel();
    }

}
