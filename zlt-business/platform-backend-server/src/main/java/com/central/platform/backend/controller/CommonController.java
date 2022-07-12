package com.central.platform.backend.controller;

import com.central.common.dto.I18nSourceDTO;
import com.central.common.model.Result;
import com.central.translate.feign.TranslateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * 无需认证得接口
 */
@Api(tags = "公共接口api")
@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private TranslateService i18nInfosService;

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
}
