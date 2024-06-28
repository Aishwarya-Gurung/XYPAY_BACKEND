package com.machpay.affiliate.beneficiary.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class BeneficiaryRequest {

    @NotBlank
    private String firstName;

    private String middleName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Size(min=6, max=10)
    private String phoneNumber;

    private String email;

    private String dateOfBirth = "1997-01-01";

    private String senderRelationship = "Self";

    @NotBlank
    private String addressLine1;

    private String addressLine2;

    @NotBlank
    private String country;

    @NotBlank
    private String state;

    private String postalCode;

    @NotBlank
    private String city;

    private Boolean isCashPickupEnabled = true;
}
