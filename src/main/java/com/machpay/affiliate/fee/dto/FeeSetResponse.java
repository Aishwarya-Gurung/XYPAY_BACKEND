package com.machpay.affiliate.fee.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FeeSetResponse {
    private String paymentMethod;

    private String payoutMethod;

    private String currency;

    private Country sourceCountry;

    private Country destinationCountry;

    private List<FeeRangeResponse> feeRanges;

    @Getter
    @Setter
    public static class Country {
        private String name;

        private String threeCharCode;

        private String twoCharCode;

        private String phoneCode;

        private String flagUrl;
    }
}
