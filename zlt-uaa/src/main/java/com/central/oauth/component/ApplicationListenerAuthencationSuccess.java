package com.central.oauth.component;

import com.central.common.model.LoginAppUser;
import com.central.user.feign.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationListenerAuthencationSuccess implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        log.info("AuthenticationSuccessEvent, {}", event.getSource());
        if (null != event.getSource()  && event.getSource() instanceof AbstractAuthenticationToken) {
            if (null != ((AbstractAuthenticationToken) event.getSource()).getPrincipal() &&
                    ((AbstractAuthenticationToken) event.getSource()).getPrincipal() instanceof LoginAppUser ) {
                LoginAppUser loginAppUser = (LoginAppUser)((AbstractAuthenticationToken) event.getSource()).getPrincipal();
                log.info("登陆成功 AuthenticationSuccessEvent： {}, {}", loginAppUser.getUsername(), loginAppUser.getType());
            }
        }
    }
}
