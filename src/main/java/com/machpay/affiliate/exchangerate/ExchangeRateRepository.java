package com.machpay.affiliate.exchangerate;

import com.machpay.affiliate.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    ExchangeRate findByReferenceId(UUID referenceId);

    List<ExchangeRate> findAllByExpiredAtNull();

    Optional<ExchangeRate> findBySourceCurrencyAndDestinationCurrencyAndExpiredAtNull(String sourceCurrency, String destinationCurrency);
}
