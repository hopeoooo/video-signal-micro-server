package com.central.oauth2.common.util;


import com.central.common.utils.PwdEncoderUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
public class PasswordTest {

    @Test
    public void should_generate_pwd(){
        PasswordEncoder encoder = PwdEncoderUtil.getDelegatingPasswordEncoder("bcrypt");
        String result = encoder.encode("admin123");
        log.info("{}",result);
        Boolean match = encoder.matches("admin123","{bcrypt}$2a$10$PloXMkrRJY5OFahHgZsb4.krNvRq6wDPhL8589sns.C2eD3I24Zb2");

        log.info("match is {}",match);
    }
}
