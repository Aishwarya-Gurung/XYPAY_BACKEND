package com.machpay.affiliate.exchangerate;

import com.machpay.affiliate.common.ListResponse;
import com.machpay.affiliate.exchangerate.dto.ExchangeRateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/exchange-rate")
public class ExchangeRateController {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @GetMapping("")
    public ResponseEntity<ListResponse> getExchangeRates() {
        List<ExchangeRateResponse> exchangeRateResponse = exchangeRateService.getExchangeRates();
        return ResponseEntity.ok(new ListResponse(exchangeRateResponse));
    }
}
