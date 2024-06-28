package com.machpay.affiliate.fee.dto;

import lombok.Data;

@Data
public class SourceCountryFeeSetResponse {

    private String destinationCountryCode;

    private String payoutMethod;

    private String paymentMethod;

    private FeeSetResponse feeSet;
}
