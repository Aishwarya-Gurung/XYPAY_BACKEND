package com.machpay.affiliate.beneficiary.cashpickup;

import com.machpay.affiliate.entity.Payer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayerRepository extends JpaRepository<Payer,Long> {
    Optional<Payer> findByReferenceId(Long referenceId);

    Optional<Payer> findByCountry(String country);

    List<Payer> findAllByCountry(String country);
}
