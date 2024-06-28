package com.machpay.affiliate.state.license;

import com.machpay.affiliate.entity.License;
import com.machpay.affiliate.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {
    Optional<License> getLicenseByState(State state);
}
