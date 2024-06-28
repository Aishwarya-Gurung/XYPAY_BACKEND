package com.machpay.affiliate.user.sender.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class KYCInfo {
    @NotNull
    @NotBlank
    private String firstName;

    private String middleName;

    @NotNull
    private String lastName;

    @NotNull
    @Size(min = 6, max = 10)
    private String phoneNumber;

    private String countryCode;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String state;

    private String status;

    private String country;

    @NotNull
    private String gender;

    @NotNull
    private String dateOfBirth;

    @NotBlank
    private String addressLine1;

    private String addressLine2;

    @NotBlank
    private String city;

    @NotBlank
    private String zipcode;

    private String kycStatus;
}
