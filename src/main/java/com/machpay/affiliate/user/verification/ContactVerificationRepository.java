package com.machpay.affiliate.user.verification;

import com.machpay.affiliate.common.enums.DeviceType;
import com.machpay.affiliate.entity.ContactVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactVerificationRepository extends JpaRepository<ContactVerification, String> {

    Boolean existsByTypeAndContact(DeviceType deviceType, String contact);

    Boolean existsByContactAndLockedIsFalseAndVerifiedIsTrue(String contact);

    Optional<ContactVerification> findByTypeAndContact(DeviceType deviceType, String contact);
}