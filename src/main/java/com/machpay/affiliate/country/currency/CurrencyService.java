package com.machpay.affiliate.country.currency;

import com.machpay.affiliate.common.Constants;
import com.machpay.affiliate.common.enums.AffiliateCountry;
import com.machpay.affiliate.common.exception.ResourceNotFoundException;
import com.machpay.affiliate.entity.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    public void save(List<Currency> currencyList) {
        currencyRepository.saveAll(currencyList);
    }

    public Currency findByCode(String code) {
        return currencyRepository.findByCode(code).orElseThrow(() -> new ResourceNotFoundException("currency", "code"
                , code));
    }

    public boolean existsByCode(String code) {
        return currencyRepository.existsByCode(code);
    }

    public List<String> getCurrenciesByCountry(String threeCharCountryCode) {
        return Constants.CORRIDOR_WISE_PAYOUT_CURRENCY.get(AffiliateCountry.get(threeCharCountryCode));
    }
}
