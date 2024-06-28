package com.machpay.affiliate.country.currency;

import com.machpay.affiliate.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

   Optional<Currency> findByCode(String code);

   Boolean existsByCode(String code);
}
