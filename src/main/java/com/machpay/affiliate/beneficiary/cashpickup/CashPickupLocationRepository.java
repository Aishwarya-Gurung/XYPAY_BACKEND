package com.machpay.affiliate.beneficiary.cashpickup;

import com.machpay.affiliate.entity.CashPickupLocation;
import com.machpay.affiliate.entity.Payer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CashPickupLocationRepository extends JpaRepository<CashPickupLocation, Long> {
    List<CashPickupLocation> findAllByActiveTrueAndPayer(Payer payer);

    boolean existsByPayer(Payer payer);
}
