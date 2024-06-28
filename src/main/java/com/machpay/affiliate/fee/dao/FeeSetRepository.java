package com.machpay.affiliate.fee.dao;

import com.machpay.affiliate.common.enums.PaymentMethod;
import com.machpay.affiliate.common.enums.PayoutMethod;
import com.machpay.affiliate.entity.FeeSet;
import com.machpay.affiliate.entity.SourceDestinationCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FeeSetRepository extends JpaRepository<FeeSet, Long> {
    List<FeeSet> findAllByExpiredAt(Date date);

    List<FeeSet> findAllBySourceDestinationCountryAndExpiredAt(SourceDestinationCountry sourceDestinationCountry,
                                                               Date expireDate);

    List<FeeSet> findAllBySourceDestinationCountryAndPaymentMethodAndPayoutMethodAndExpiredAt(SourceDestinationCountry sourceDestinationCountry, PaymentMethod paymentMethod, PayoutMethod payoutMethod, Date expireDate);

}
