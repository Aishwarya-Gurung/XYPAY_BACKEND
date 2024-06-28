package com.machpay.affiliate.bank;

import com.machpay.affiliate.entity.Bank;
import com.machpay.affiliate.entity.BankCurrency;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankCurrencyRepository extends JpaRepository<BankCurrency, Long> {
    Optional<BankCurrency> findByBank(Bank bank);

    @Query("select bc from BankCurrency bc where bc.currency.code = ?1")
    List<BankCurrency> findByCurrency(String currency);
}
