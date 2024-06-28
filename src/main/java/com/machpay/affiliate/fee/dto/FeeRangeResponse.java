package com.machpay.affiliate.fee.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FeeRangeResponse {
    private BigDecimal minAmount;

    private BigDecimal maxAmount;

    private BigDecimal flatFee;

    private BigDecimal percentageFee;
}
