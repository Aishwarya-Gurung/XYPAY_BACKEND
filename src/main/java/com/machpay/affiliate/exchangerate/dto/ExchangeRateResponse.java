package com.machpay.affiliate.exchangerate.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExchangeRateResponse {

    private BigDecimal rate;

    private String sourceCurrency;

    private String destinationCurrency;

}
