package com.machpay.affiliate.config;

import com.machpay.affiliate.bank.BankService;
import com.machpay.affiliate.beneficiary.cashpickup.CashPickupService;
import com.machpay.affiliate.common.enums.AffiliateCountry;
import com.machpay.affiliate.country.CountryService;
import com.machpay.affiliate.exchangerate.ExchangeRateService;
import com.machpay.affiliate.fee.FeeService;
import com.machpay.affiliate.state.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class ConfigController {
}
