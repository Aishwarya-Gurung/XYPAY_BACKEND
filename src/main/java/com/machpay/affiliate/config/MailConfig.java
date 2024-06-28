package com.machpay.affiliate.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Configuration
@ConfigurationProperties(prefix = "mail")
public class MailConfig {

    private String host;

    private String port;

    private String username;

    private String password;

    private String from;

    private String fromName;

    private String verificationApi;

    private String resetPasswordApi;

    private String baseUrl;

    private String moneytunSupport;

    private String moneytunBaseUrl;

    private String customerSupportEmail;

    private String customerSupportPhoneNumber;

    private String gmtTermsAndPrivacyPolicy;

    private String prabhuTermsAndPrivacyPolicy;

    private String protocol;

    private boolean ssl;

    private boolean starttls;

    private boolean auth;

    private String supportAdmins;

    private String supportAssistants;

    private String termsAndPrivacyPolicy;

    public String getLogoBaseUrl() {
        return this.baseUrl;
    }
}