//package com.central.common.feign;
//
//import com.central.common.constant.ServiceNameConstants;
//import com.central.common.feign.fallback.UaaServiceFallbackFactory;
//import com.central.common.model.Result;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//
//@FeignClient(name = ServiceNameConstants.UAA_SERVICE, fallbackFactory = UaaServiceFallbackFactory.class, decode404 = true)
//public interface UaaService {
//    /**
//     * 在线会员数量
//     *
//     * @return
//     */
//    @GetMapping(value = "/tokens/players")
//    Result<Integer> queryPlayerNums();
//}
