package com.central.config.feign;

import com.central.common.constant.ServiceNameConstants;
import com.central.common.model.Result;
import com.central.common.model.SysNotice;
import com.central.config.dto.TouristDto;
import com.central.config.feign.callback.ConfigServiceFallbackFactory;
import com.central.log.annotation.AuditLog;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

;import java.util.List;
import java.util.Map;

/**
 */
@FeignClient(name = ServiceNameConstants.CONFIG_SERVICE, fallbackFactory = ConfigServiceFallbackFactory.class, decode404 = true)
public interface ConfigService {
    /**
     * 查询游戏列表
     */
    @PostMapping(value = "/system/list")
    String list();

    @GetMapping("/system/findTouristAmount")
    Result<TouristDto> findTouristAmount() ;

    @PostMapping("/system/saveTourist")
    Result saveTourist(@RequestParam("params") Map<String, String> params) ;

    @GetMapping("/system/findMoneySymbol")
    Result findMoneySymbol();

    @PostMapping("/system/updateMoneySymbol")
    Result updateMoneySymbol( @RequestParam("moneySymbol")String moneySymbol);

    @GetMapping("/system/findLogoUrlInfo")
     Result findLogoUrlInfo();



    @GetMapping("/notice/findNoticeList")
    Result findNoticeList(@RequestParam("params") Map<String, Object> params) ;

    @DeleteMapping(value = "/notice/deleteNoticeId/{id}")
     Result deleteNoticeId(@PathVariable("id") Long id);


    @GetMapping("/notice/updateEnabled")
    Result updateEnabled(@RequestParam("params") Map<String, Object> params) ;

    @PostMapping("/notice/saveOrUpdate")
    Result saveOrUpdate(@RequestBody SysNotice sysNotice);

}