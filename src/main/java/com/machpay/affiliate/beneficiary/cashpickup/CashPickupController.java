package com.machpay.affiliate.beneficiary.cashpickup;

import com.machpay.affiliate.beneficiary.cashpickup.dto.PayerResponse;
import com.machpay.affiliate.common.ListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/payers")
public class CashPickupController {

    @Autowired
    private CashPickupService cashPickupService;

    @GetMapping("")
    @PreAuthorize("hasRole('USER')")
    public ListResponse getPayers(@RequestParam("country") String country) {
        List<PayerResponse> payerList = cashPickupService.getAllByCountry(country);

        return new ListResponse(payerList);
    }
}
