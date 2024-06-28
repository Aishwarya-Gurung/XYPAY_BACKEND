package com.machpay.affiliate.entity;

import com.machpay.affiliate.common.enums.DeviceType;
import com.machpay.affiliate.common.enums.DeviceVerificationStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class TwoFaVerification extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private DeviceType type;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeviceVerificationStatus status = DeviceVerificationStatus.PENDING;

    @Column
    private Integer verificationAttempt;

    @Column
    private LocalDateTime expiryDate;

    @Column
    private LocalDateTime verifiedAt;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private int resendAttempt;
}
