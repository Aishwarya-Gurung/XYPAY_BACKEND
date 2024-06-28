package com.machpay.affiliate.bank;

import com.machpay.affiliate.bank.dto.BankResponse;
import com.machpay.affiliate.common.enums.AffiliateCountry;
import com.machpay.affiliate.common.enums.Currency;
import com.machpay.affiliate.common.exception.BadRequestException;
import com.machpay.affiliate.common.exception.ResourceNotFoundException;
import com.machpay.affiliate.entity.Bank;
import com.machpay.affiliate.entity.BankCurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BankService {

    @Autowired
    private BankMapper bankMapper;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private BankCurrencyRepository bankCurrencyRepository;

    public List<BankResponse> setBankCurrency(List<Bank> bank) {
        List<BankResponse> bankResponses = bankMapper.toBankResponseList(bank);

        bankResponses.forEach(bankResponse -> {
            Bank banks = bankRepository.findByReferenceId(bankResponse.getReferenceId());
            bankResponse.setCurrency(getPayoutCurrency(banks));
        });

        return bankResponses;
    }

    public List<BankResponse> getBanks(String country, String currency) {
        if (AffiliateCountry.get(country.toUpperCase()) == null) {
            throw new BadRequestException("Invalid country code");
        }

        if (!Currency.exists(currency.toUpperCase())) {
            throw new BadRequestException("Invalid currency code");
        }

        List<BankCurrency> currencyWiseBanks = bankCurrencyRepository.findByCurrency(currency);
        currencyWiseBanks = currencyWiseBanks
                .stream()
                .filter(currencyWiseBank -> currencyWiseBank.getBank().getCountry().equals(country))
                .collect(Collectors.toList());
        List<Bank> banks = currencyWiseBanks
                .stream()
                .map(BankCurrency::getBank)
                .collect(Collectors.toList());

        return setBankCurrency(banks);
    }

    public List<BankResponse> getBanks(String country) {
        if (AffiliateCountry.get(country.toUpperCase()) == null) {
            throw new BadRequestException("Invalid country code");
        }

        List<Bank> banks = bankRepository.findAllByCountryOrderByNameAsc(country);

        return setBankCurrency(banks);
    }

    public Bank findById(Long bankId) {
        return bankRepository.findByReferenceId(bankId);
    }

    public String getPayoutCurrency(Bank bank) {
        Optional<BankCurrency> bankCurrency = bankCurrencyRepository.findByBank(bank);

        if (bankCurrency.isPresent()) {
            return bankCurrency.get().getCurrency().getCode();
        }

        throw new ResourceNotFoundException("Payout Currency for bank", "bank_id", bank.getId());
    }
}
