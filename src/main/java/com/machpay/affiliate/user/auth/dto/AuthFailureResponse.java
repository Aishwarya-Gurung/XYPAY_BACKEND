package com.machpay.affiliate.user.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class AuthFailureResponse {
    private String token;

    private String tokenType = "Bearer";

    private List<String> roles;

    private String error;

    private String device_id;
}
