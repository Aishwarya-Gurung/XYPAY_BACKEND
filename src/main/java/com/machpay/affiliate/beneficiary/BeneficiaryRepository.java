package com.machpay.affiliate.beneficiary;

import com.machpay.affiliate.entity.Beneficiary;
import com.machpay.affiliate.entity.Sender;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {

    List<Beneficiary> findAllBySenderAndActiveTrueOrderByCreatedAtDesc(Sender sender);

    Optional<Beneficiary> findBeneficiaryByReferenceId(UUID referenceId);

    int countAllBySenderAndActiveTrue(Sender sender);

    List<Beneficiary> findAllBySender(Sender sender);
}
