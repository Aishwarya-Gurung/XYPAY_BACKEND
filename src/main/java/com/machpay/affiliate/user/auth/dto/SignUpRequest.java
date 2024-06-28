package com.machpay.affiliate.user.auth.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class SignUpRequest {
    @NotNull
    private String firstName;

    private String middleName;

    @NotNull
    private String lastName;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 6, max = 10)
    private String phoneNumber;

    @NotNull
    private String countryCode;

    @NotNull
    private String password;

    @NotNull
    private String state;

    @NotBlank
    private String device;

    private String reCAPTCHAToken;
}