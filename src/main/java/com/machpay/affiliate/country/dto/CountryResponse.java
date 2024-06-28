package com.machpay.affiliate.country.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CountryResponse {

    private String name;

    private String threeCharCode;

    private String twoCharCode;

    private String phoneCode;

    private String flagUrl;

    private List<CurrencyResponse> currency;

    private PayoutMethodResponse payoutMethod;
}
