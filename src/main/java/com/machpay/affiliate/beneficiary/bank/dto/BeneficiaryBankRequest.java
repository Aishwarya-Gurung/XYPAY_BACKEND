package com.machpay.affiliate.beneficiary.bank.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BeneficiaryBankRequest {
    @NotBlank
    private String beneficiaryId;

    @NotBlank
    private String accountNumber;

    @NotBlank
    private String accountType;

    @NotBlank
    private String bankId;

    private String branchLocation;

}