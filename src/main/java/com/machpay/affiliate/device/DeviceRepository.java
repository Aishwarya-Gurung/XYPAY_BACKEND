package com.machpay.affiliate.device;

import com.machpay.affiliate.entity.Device;
import com.machpay.affiliate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findByUserAndId(User user, UUID id);

    List<Device> findAllByUserAndDeviceActiveIsTrueOrderByLastLoginDateDesc(User user);

    List<Device> findAllByUserAndVerificationRequiredIsTrueAndDeviceActiveIsFalseAndVerificationAttemptIsGreaterThanOrVerificationCodeResendAttemptsIsGreaterThan(
            User user, int verificationAttempt, int verificationCodeResendAttempts);

    Device findByUserAndFingerprint(User user, String fingerprint);

    List<Device> findAllByUser(User user);
}
