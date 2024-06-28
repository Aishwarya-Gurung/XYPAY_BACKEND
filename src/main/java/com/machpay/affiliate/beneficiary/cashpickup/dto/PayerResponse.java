package com.machpay.affiliate.beneficiary.cashpickup.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayerResponse {
    private Long referenceId;

    private String name;

    private String receivingCurrency;

    private String code;

    private String country;

    private String address;

    private String phoneNumber;

    private String payingEntity;
}
