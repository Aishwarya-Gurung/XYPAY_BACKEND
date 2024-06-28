package com.machpay.affiliate.user.password.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class CredentialVerificationRequest {

    @NotBlank
    private String password;
}
