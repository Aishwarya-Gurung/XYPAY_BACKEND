package com.machpay.affiliate.bank.branch;

import com.machpay.affiliate.entity.BankBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<BankBranch, Long> {
    BankBranch findByReferenceId(String referenceId);
}
