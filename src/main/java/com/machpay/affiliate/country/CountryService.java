package com.machpay.affiliate.country;

import com.google.common.collect.ImmutableMap;
import com.machpay.affiliate.common.Constants;
import com.machpay.affiliate.common.enums.AffiliateCountry;
import com.machpay.affiliate.common.enums.PayoutMethod;
import com.machpay.affiliate.common.exception.ResourceNotFoundException;
import com.machpay.affiliate.country.currency.CurrencyMapper;
import com.machpay.affiliate.country.currency.CurrencyService;
import com.machpay.affiliate.country.dto.CountryResponse;
import com.machpay.affiliate.country.dto.CurrencyResponse;
import com.machpay.affiliate.country.dto.PayoutMethodResponse;
import com.machpay.affiliate.country.dto.SourceDestinationCountryResponse;
import com.machpay.affiliate.country.sourcedestinationcountry.SourceDestinationCountryRepository;
import com.machpay.affiliate.entity.Country;
import com.machpay.affiliate.entity.Currency;
import com.machpay.affiliate.entity.SourceDestinationCountry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class CountryService {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private SourceDestinationCountryRepository sourceDestinationCountryRepository;

    @Autowired
    private CountryMapper countryMapper;

    @Autowired
    private CurrencyMapper currencyMapper;

    final ImmutableMap<PayoutMethod, Boolean> SIERRA_LEONE_PAYOUT_METHODS = ImmutableMap.of(
            PayoutMethod.BANK_DEPOSIT, true,
            PayoutMethod.CASH_PICKUP, true,
            PayoutMethod.HOME_DELIVERY, false,
            PayoutMethod.WALLET, true);


    public Country findByThreeCharCode(String code) {
        return countryRepository.findByThreeCharCode(code).orElseThrow(() -> new ResourceNotFoundException(Constants.COUNTRY,
                "threeCharCode", code));
    }

    public Country findByReferenceId(String referenceId) {
        return countryRepository.findByReferenceId(referenceId).orElseThrow(() -> new ResourceNotFoundException(
                Constants.COUNTRY,
                "findByReferenceId", referenceId));
    }

    public Country findByName(String name) {
        return countryRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("Country",
                "name", name));
    }

    public List<CountryResponse> getAllDestinationCountryList(Country country) {
        List<Country> destinationCountries = sourceDestinationCountryRepository
                .getAllBySourceCountry(country)
                .stream()
                .map(SourceDestinationCountry::getDestinationCountry)
                .collect(Collectors.toList());
        List<CountryResponse> countryResponses = countryMapper.toCountryResponseList(destinationCountries);
        countryResponses.forEach(this::buildCurrencyResponse);

        return countryResponses.stream().map(this::buildPayoutMethodResponse).collect(Collectors.toList());
    }

    private CountryResponse buildCurrencyResponse(CountryResponse countryResponse) {
        List<String> payoutCurrencies =
                Constants.CORRIDOR_WISE_PAYOUT_CURRENCY.get(AffiliateCountry.get(countryResponse.getThreeCharCode()));

        List<CurrencyResponse> currencies = new ArrayList<>();
        payoutCurrencies.forEach(currencyCode -> {
            currencies.add(getCurrencyResponse(currencyCode));
            countryResponse.setCurrency(currencies);
        });

        return countryResponse;
    }

    public CountryResponse buildPayoutMethodResponse(CountryResponse countryResponse) {
        AffiliateCountry country = AffiliateCountry.get(countryResponse.getThreeCharCode());

        switch (country) {
            case SIERRA_LEONE:
                countryResponse.setPayoutMethod(buildPayoutMethod(SIERRA_LEONE_PAYOUT_METHODS));
                break;

            default:
                break;
        }

        return countryResponse;
    }

    private PayoutMethodResponse buildPayoutMethod(Map<PayoutMethod, Boolean> map) {
        PayoutMethodResponse payoutMethodResponse = new PayoutMethodResponse();
        map.forEach((payoutMethod, status) -> {
            if (payoutMethod.equals(PayoutMethod.BANK_DEPOSIT)) {
                payoutMethodResponse.setBankDepositEnabled(status);
            }

            if (payoutMethod.equals(PayoutMethod.CASH_PICKUP)) {
                payoutMethodResponse.setCashPickupEnabled(status);
            }

            if (payoutMethod.equals(PayoutMethod.HOME_DELIVERY)) {
                payoutMethodResponse.setHomeDeliveryEnabled(status);
            }

            if (payoutMethod.equals(PayoutMethod.WALLET)) {
                payoutMethodResponse.setMobileWalletEnabled(status);
            }
        });

        return payoutMethodResponse;
    }

    public List<CountryResponse> getAllSourceCountryList() {
        List<Country> sourceCountries = sourceDestinationCountryRepository.findDistinctSourceCountry();

        return sourceCountries.stream().map(this::buildSourceCountryResponse).collect(Collectors.toList());
    }

    public List<SourceDestinationCountryResponse> getAllSourceDestinationCountries() {
        List<SourceDestinationCountry> sourceDestinationCountries = sourceDestinationCountryRepository.findAll();

        return sourceDestinationCountries.stream()
                .map(this::buildSourceDestinationCountryResponse).collect(Collectors.toList());
    }

    private SourceDestinationCountryResponse buildSourceDestinationCountryResponse(SourceDestinationCountry sourceDestinationCountry) {
        SourceDestinationCountryResponse sourceDestinationCountryResponse = new SourceDestinationCountryResponse();
        sourceDestinationCountryResponse.setId(sourceDestinationCountry.getId());
        sourceDestinationCountryResponse.setSource(buildSourceCountryResponse(sourceDestinationCountry.getSourceCountry()));
        sourceDestinationCountryResponse.setDestination(buildDestinationCountryResponse(sourceDestinationCountry.getDestinationCountry()));

        return sourceDestinationCountryResponse;
    }

    private CountryResponse buildSourceCountryResponse(Country country) {
        CountryResponse countryResponse = countryMapper.toCountryResponse(country);
        List<CurrencyResponse> currencyResponses = new ArrayList<>();
        currencyResponses.add(getCurrencyResponse(country.getCurrency().getCode()));
        countryResponse.setCurrency(currencyResponses);

        return countryResponse;
    }

    private CountryResponse buildDestinationCountryResponse(Country country) {
        CountryResponse countryResponse = countryMapper.toCountryResponse(country);
        AffiliateCountry destinationCountry = AffiliateCountry.get(countryResponse.getThreeCharCode());
        List<String> payoutCurrencies = Constants.CORRIDOR_WISE_PAYOUT_CURRENCY.get(destinationCountry);
        List<CurrencyResponse> currencyResponses = payoutCurrencies.stream()
                .map(this::getCurrencyResponse).collect(Collectors.toList());
        countryResponse.setCurrency(currencyResponses);
        countryResponse.setPayoutMethod(buildPayoutMethodResponse(countryResponse).getPayoutMethod());

        return countryResponse;
    }

    private CurrencyResponse getCurrencyResponse(String currencyCode) {
        Currency currency = currencyService.findByCode(currencyCode);

        return currencyMapper.toCurrencyResponse(currency);
    }

    public SourceDestinationCountry getSourceDestinationCountryById(Long sourceDestinationCountryId) {
        return sourceDestinationCountryRepository.findById(sourceDestinationCountryId)
                .orElseThrow(() -> new ResourceNotFoundException("SourceDestinationCountry", "id",
                        sourceDestinationCountryId));
    }
}

