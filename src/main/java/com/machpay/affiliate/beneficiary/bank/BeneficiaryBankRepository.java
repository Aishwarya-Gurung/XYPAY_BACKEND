package com.machpay.affiliate.beneficiary.bank;

import com.machpay.affiliate.entity.Beneficiary;
import com.machpay.affiliate.entity.BeneficiaryBank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeneficiaryBankRepository extends JpaRepository<BeneficiaryBank, Long> {

    List<BeneficiaryBank> findByBeneficiary(Beneficiary beneficiary);

    BeneficiaryBank findByReferenceId(String referenceId);
}
