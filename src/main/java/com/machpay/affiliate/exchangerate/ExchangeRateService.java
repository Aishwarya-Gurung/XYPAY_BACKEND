package com.machpay.affiliate.exchangerate;

import com.machpay.affiliate.common.exception.ResourceNotFoundException;
import com.machpay.affiliate.entity.ExchangeRate;
import com.machpay.affiliate.exchangerate.dto.ExchangeRateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ExchangeRateService {

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Autowired
    private ExchangeRateMapper exchangeRateMapper;

    public List<ExchangeRateResponse> getExchangeRates() {
        return exchangeRateMapper.toExchangeRateResponseList(exchangeRateRepository.findAllByExpiredAtNull());
    }

    public ExchangeRate getBySourceAndDestinationCurrency(String sourceCurrency, String destinationCurrency) {
        return exchangeRateRepository.findBySourceCurrencyAndDestinationCurrencyAndExpiredAtNull(sourceCurrency, destinationCurrency)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange rate", "provided destination currency", destinationCurrency));
    }
}
