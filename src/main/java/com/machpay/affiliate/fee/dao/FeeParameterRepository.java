package com.machpay.affiliate.fee.dao;

import com.machpay.affiliate.common.enums.PaymentMethod;
import com.machpay.affiliate.common.enums.PayoutMethod;
import com.machpay.affiliate.entity.Currency;
import com.machpay.affiliate.entity.FeeParameter;
import com.machpay.affiliate.entity.SourceDestinationCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeParameterRepository extends JpaRepository<FeeParameter, Long> {
    boolean existsByPaymentMethodAndPayoutMethodAndSourceDestinationCountryAndCurrency(PaymentMethod paymentMethod,
                                                                                       PayoutMethod payoutMethod,
                                                                                       SourceDestinationCountry sourceDestinationCountry,
                                                                                       Currency currency);

    FeeParameter findByPaymentMethodAndPayoutMethodAndSourceDestinationCountryAndCurrency(PaymentMethod paymentMethod,
                                                                                          PayoutMethod payoutMethod,
                                                                                          SourceDestinationCountry sourceDestinationCountry,
                                                                                          Currency currency);

    List<FeeParameter> findAllBySourceDestinationCountry(SourceDestinationCountry sourceDestinationCountry);
}
