package com.central.agent.controller;

import com.central.config.feign.ConfigService;
import com.central.file.feign.FileService;
import com.central.search.client.feign.SearchService;
import com.central.user.feign.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 代理模块
 */
@Slf4j
@RestController
@Api(tags = "代理模块api")
@RequestMapping("/agent")
public class AgentController {

    @Resource
    private SearchService searchService;

    @Resource
    private UserService userService;

    @Resource
    private ConfigService configService;

    @Resource
    private FileService fileService;

    @ApiOperation(value = "查询代理列表")
    @GetMapping("/list")
    public String list(){
        return "test.game";
    }


    @ApiOperation(value = "查询代理列表")
    @GetMapping("/test1")
    public String test1(){
        return configService.list() + fileService.test();
    }
}
