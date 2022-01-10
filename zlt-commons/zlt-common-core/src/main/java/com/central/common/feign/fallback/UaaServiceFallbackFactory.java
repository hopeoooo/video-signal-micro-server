//package com.central.common.feign.fallback;
//
//import com.central.common.feign.UaaService;
//import com.central.common.model.Result;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.openfeign.FallbackFactory;
//
//@Slf4j
//public class UaaServiceFallbackFactory implements FallbackFactory<UaaService> {
//    @Override
//    public UaaService create(Throwable cause) {
//        return new UaaService() {
//            @Override
//            public Result<Integer> queryPlayerNums() {
//                log.error("查询在线会员人数失败:{}",cause);
//                return Result.failed("查询在线会员人数失败");
//            }
//        };
//    }
//}
