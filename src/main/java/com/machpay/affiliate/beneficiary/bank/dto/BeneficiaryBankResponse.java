package com.machpay.affiliate.beneficiary.bank.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BeneficiaryBankResponse {

    private String referenceId;

    private String accountNumber;

    private String accountType;

    private String bankName;

    private String currency;
}
