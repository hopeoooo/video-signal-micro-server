package com.central.translate.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 翻译模块
 */
@Slf4j
@RestController
@Api(tags = "翻译api")
@RequestMapping("/translate")
public class TranslateController {

    @ApiOperation(value = "测试翻译")
    @GetMapping("/test")
    public String test(){
        return "test.game";
    }
}
