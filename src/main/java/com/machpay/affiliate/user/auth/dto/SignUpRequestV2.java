package com.machpay.affiliate.user.auth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SignUpRequestV2 extends SignUpRequest {

    private String gender;

    private String dateOfBirth;

    private String addressLine1;

    private String addressLine2;

    private String zipcode;

    private String city;

}
