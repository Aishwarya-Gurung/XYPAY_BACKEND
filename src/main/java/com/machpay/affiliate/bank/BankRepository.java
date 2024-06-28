package com.machpay.affiliate.bank;

import com.machpay.affiliate.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {

    Bank findByName(String name);

    Bank findByReferenceId(Long referenceId);

    List<Bank> findAllByCountryOrderByNameAsc(String country);
}
