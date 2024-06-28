package com.machpay.affiliate.fee.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
public class FeeParameterRequest {
    @NotNull
    private String paymentMethod;

    @NotNull
    private Long sourceDestinationCountry;
}
