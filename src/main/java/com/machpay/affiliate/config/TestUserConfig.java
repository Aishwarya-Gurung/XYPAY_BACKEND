package com.machpay.affiliate.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Component
@Configuration
@ConfigurationProperties(prefix = "test")
public class TestUserConfig {
    private final User user = new User();

    @Getter
    @Setter
    public static class User {
        private String email;
    }
}
