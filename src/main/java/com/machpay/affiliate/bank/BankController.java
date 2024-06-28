package com.machpay.affiliate.bank;

import com.machpay.affiliate.bank.dto.BankResponse;
import com.machpay.affiliate.common.ListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/banks")
public class BankController {
    @Autowired
    private BankService bankService;

    @GetMapping("/{country}")
    public ListResponse getAllBanksByCountry(@PathVariable String country) {
        List<BankResponse> bankResponses = bankService.getBanks(country);

        return new ListResponse(bankResponses);
    }

    @GetMapping
    public ListResponse getCountrySpecificBanks(@RequestParam("country") String country,
                                                  @RequestParam("currency") String currency) {
        List<BankResponse> bankResponses = bankService.getBanks(country, currency);

        return new ListResponse(bankResponses);
    }
}
