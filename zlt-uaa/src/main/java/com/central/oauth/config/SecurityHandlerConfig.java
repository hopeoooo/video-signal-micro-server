package com.central.oauth.config;

import com.central.common.model.CodeEnum;
import com.central.oauth.exception.ExtendOAuth2Exception;
import com.central.oauth.exception.CustomExtendOAuth2Exception;
import com.central.oauth.exception.ValidateCodeException;
import com.central.oauth.handler.OauthLogoutHandler;
import com.central.oauth.handler.OauthLogoutSuccessHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.UnsupportedResponseTypeException;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证错误处理
 *
 * @author zlt
 */
@Slf4j
@Configuration
public class SecurityHandlerConfig {
    @Bean
    public LogoutHandler logoutHandler() {
        return new OauthLogoutHandler();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new OauthLogoutSuccessHandler();
    }

    @Bean
    public WebResponseExceptionTranslator webResponseExceptionTranslator() {
        return new DefaultWebResponseExceptionTranslator() {
            private static final String BAD_MSG = "坏的凭证";
            private static final String BAD_MSG_EN = "Bad credentials";

            @Override
            public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
                OAuth2Exception oAuth2Exception;
                if (e.getMessage() != null
                        && (BAD_MSG.equals(e.getMessage()) || BAD_MSG_EN.equals(e.getMessage()))) {
                    oAuth2Exception = new InvalidGrantException("用户名或密码错误", e);
                } else if (e instanceof InternalAuthenticationServiceException
                    || e instanceof ValidateCodeException) {
                    oAuth2Exception = new InvalidGrantException(e.getMessage(), e);
                } else if (e instanceof OAuth2Exception) {
                    oAuth2Exception = (OAuth2Exception)e;
                } else {
                    oAuth2Exception = new UnsupportedResponseTypeException("服务内部错误", e);
                }
                super.translate(oAuth2Exception);
//                ResponseEntity<OAuth2Exception> response = super.translate(oAuth2Exception);
//                ResponseEntity.status(oAuth2Exception.getHttpErrorCode());
//                response.getBody().addAdditionalInformation("resp_code", CodeEnum.ERROR_AUTH.getCode().toString());
//                response.getBody().addAdditionalInformation("resp_msg", oAuth2Exception.getMessage());
//                return response;

                // 处理异常，返回httpstatys=200的Result结构
                CustomExtendOAuth2Exception customExtendOAuth2Exception = new CustomExtendOAuth2Exception(oAuth2Exception.getMessage(), e);
                int status = customExtendOAuth2Exception.getHttpErrorCode();
//                HttpHeaders headers = new HttpHeaders();
//                headers.set("Cache-Control", "no-store");
//                headers.set("Pragma", "no-cache");
//                if (status == HttpStatus.UNAUTHORIZED.value() || (e instanceof InsufficientScopeException)) {
//                    headers.set("WWW-Authenticate", String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, e.getSummary()));
//                }
                return new ResponseEntity<>(new ExtendOAuth2Exception(customExtendOAuth2Exception.getMessage(), CodeEnum.ERROR_AUTH.getCode(),
                        customExtendOAuth2Exception.getOAuth2ErrorCode()), null, HttpStatus.valueOf(status));
            }

        };
    }

    /**
     * 登陆成功
     */
    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return new SavedRequestAwareAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                super.onAuthenticationSuccess(request, response, authentication);
            }
        };
    }
}
