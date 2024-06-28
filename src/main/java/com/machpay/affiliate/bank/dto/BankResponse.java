package com.machpay.affiliate.bank.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankResponse {

    private Long referenceId;

    private String name;

    private String currency;
}
