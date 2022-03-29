package com.central.business.config;

import com.central.business.constant.BusinessConstants;
import com.central.business.properties.BusinessProperties;
import com.central.business.utils.CheckSignatureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 校验商户信息
 *
 * @author xuxueli 2015-12-12 18:09:04
 */
@Component
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private BusinessProperties businessProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return super.preHandle(request, response, handler);
        }
        if (!checkAuthentication(request)) {
            throw new AccessDeniedException("认证失败");
        }
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // TODO Auto-generated method stub
        BusinessContextHolder.clear();
        super.afterCompletion(request, response, handler, ex);

    }

    private boolean checkAuthentication(HttpServletRequest request) {
        //MD5加密传输 32位小写
        String vendorId = request.getHeader(BusinessConstants.VENDOR_ID);
        String secretKey = request.getHeader(BusinessConstants.SECRET_KEY);
        String signature = request.getHeader(BusinessConstants.SIGNATURE);
        String signatureKey = BusinessConstants.SECRET_KEY + "=" + secretKey + "&" + BusinessConstants.VENDOR_ID + "=" + vendorId;
        boolean checkSignature = CheckSignatureUtil.checkSignature(signatureKey, signature);
        if (!checkSignature) {
            return checkSignature;
        }
        //商户账号秘钥判断
        String[] businessList = businessProperties.getBusinessList();
        for (String str : businessList) {
            String[] business = str.split("/");
            if (vendorId.equals(business[0]) && secretKey.equals(business[1])) {
                BusinessContextHolder.setBusiness(vendorId);
                return true;
            }
        }
        //允许查询赛果的账号秘钥判断
        String[] allowLotteryDataList = businessProperties.getAllowLotteryDataList();
        for (String str : allowLotteryDataList) {
            String[] allow = str.split("/");
            String requestURI = request.getRequestURI();
            //只允许访问指定接口
            if (vendorId.equals(allow[0]) && secretKey.equals(allow[1]) && requestURI.contains(BusinessConstants.LOTTER_DATA_API_URL)) {
                BusinessContextHolder.setBusiness(vendorId);
                return true;
            }
        }
        return false;
    }
}
