package com.central.translate.controller;

import com.central.common.model.Result;
import com.central.translate.model.co.I18nPositionCo;
import com.central.common.vo.I18nPositionVO;
import com.central.translate.service.I18nPositionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@Api(tags = "翻译位置api")
public class TranslatePositionController {

    @Autowired
    private I18nPositionService i18nPositionService;

    /**
     * 翻译页面位置列表
     *
     * @return {@link List} 页面列表
     * @author lance
     * @since 2022 -01-25 13:13:35
     */
    @GetMapping("/translate/position/pageList")
    @ApiOperation(value = "查询所有的翻译位置(页面)")
    public List<I18nPositionVO> pageList() {
        return i18nPositionService.findAllPage();
    }


    /**
     * 页面下的所有位置
     *
     * @param pid 页面id
     * @return {@link List} 位置列表
     * @author lance
     * @since 2022 -01-25 13:13:35
     */
    @GetMapping("/translate/position/byPid")
    @ApiOperation(value = "翻译页面下的所有位置")
    public List<I18nPositionVO> positions(@ApiParam(value = "页面id") Long pid) {
        return i18nPositionService.findByPid(pid);
    }

    /**
     * 新增位置
     *
     * @param param 新增参数
     * @return {@link Result} 操作结果
     * @author lance
     * @since 2022 -01-25 13:32:11
     */
    @PostMapping("/translate/position/saveOrUpdate")
    public Result<String> saveOrUpdate(@Valid @RequestBody I18nPositionCo param) {
        try {
            i18nPositionService.saveOrUpdate(param);
            return Result.succeed("操作成功");
        } catch (Exception ex) {
            log.error("i18n-saveOrUpdate-error", ex);
            return Result.failed("操作失败");
        }
    }

}
