package com.machpay.affiliate.fee.dto;

import com.machpay.affiliate.common.enums.PaymentMethod;
import com.machpay.affiliate.common.enums.PayoutMethod;
import com.machpay.affiliate.country.dto.CurrencyResponse;
import com.machpay.affiliate.country.dto.SourceDestinationCountryResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeeParameterResponse {
    private Long id;

    private PaymentMethod paymentMethod;

    private PayoutMethod payoutMethod;

    private CurrencyResponse currency;

    private SourceDestinationCountryResponse sourceDestinationCountry;
}
