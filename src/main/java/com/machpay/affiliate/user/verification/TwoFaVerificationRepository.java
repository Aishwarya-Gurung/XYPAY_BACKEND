package com.machpay.affiliate.user.verification;

import com.machpay.affiliate.common.enums.DeviceType;
import com.machpay.affiliate.entity.TwoFaVerification;
import com.machpay.affiliate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TwoFaVerificationRepository extends JpaRepository<TwoFaVerification, String> {

    TwoFaVerification findByToken(String token);

    Optional<TwoFaVerification> findByUserIdAndType(Long id, Enum type);

    Boolean existsByUserAndType(User user, DeviceType deviceType);

    Optional<TwoFaVerification> findByUserAndType(User user, DeviceType deviceType);
}