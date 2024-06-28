package com.machpay.affiliate.country;

import com.machpay.affiliate.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findByName(String name);

    Optional<Country> findByThreeCharCode(String threeCharCode);

    Optional<Country> findByReferenceId(String referenceId);
}
