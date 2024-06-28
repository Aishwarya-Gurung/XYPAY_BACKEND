package com.machpay.affiliate.fee.dto;

import com.machpay.affiliate.common.enums.PaymentMethod;
import com.machpay.affiliate.common.enums.PayoutMethod;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class FeeRequest {
    @NotNull
    private String currency;

    @NotNull
    private PayoutMethod payoutMethod;

    @NotNull
    private PaymentMethod paymentMethod;

    @NotNull
    private Long sourceDestinationCountry;

    @Valid
    private List<FeeRangeRequest> feeRanges;
}
