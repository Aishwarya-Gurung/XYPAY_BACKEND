package com.machpay.affiliate.entity;

import com.machpay.affiliate.common.enums.DeviceType;
import com.machpay.affiliate.common.enums.LockReason;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ContactVerification extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDateTime expiryDate;

    @Column
    @Enumerated(EnumType.STRING)
    private DeviceType type;

    @Column
    private int verificationAttempt;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private String contact;

    @Column
    private int resendAttempt;

    @Column
    private boolean verified;

    @Column
    private LocalDateTime verifiedAt;

    @Column
    private boolean locked;

    @Column
    private LocalDateTime lockedAt;

    @Column
    @Enumerated(EnumType.STRING)
    private LockReason lockReason;
}
