package com.machpay.affiliate.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDeleteAction;
import com.machpay.affiliate.common.enums.ClientType;

import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Device extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type = "uuid-char")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column
    private String ip;

    @Column
    private String os;

    @Column
    private String browser;

    @Column
    private String browserVersion;

    @Column
    private String deviceType;

    @Column(nullable = false)
    private String fingerprint;

    @Column
    private String verificationCode;

    @Column
    private LocalDateTime expiryDate;

    @Column
    private boolean deviceVerified;

    @Column
    private boolean deviceActive;

    @Column
    private LocalDateTime lastLoginDate;

    @Column
    private boolean verificationRequired;

    @Column
    private int verificationCodeResendAttempts;

    @Column
    @ColumnDefault("0")
    private int verificationAttempt = 0;

    @Column
    @Enumerated(EnumType.STRING)
    private ClientType clientType;
}
