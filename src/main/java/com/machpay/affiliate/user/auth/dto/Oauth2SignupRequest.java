package com.machpay.affiliate.user.auth.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class Oauth2SignupRequest {

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String state;
}
