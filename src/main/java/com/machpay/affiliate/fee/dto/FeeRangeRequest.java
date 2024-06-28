package com.machpay.affiliate.fee.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class FeeRangeRequest {
    @NotNull
    @DecimalMin("0.00")
    private BigDecimal minAmount;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal maxAmount;

    @DecimalMin("0.00")
    private BigDecimal flatFee = BigDecimal.ZERO;

    @DecimalMin("0.00")
    private BigDecimal percentageFee = BigDecimal.ZERO;
}
