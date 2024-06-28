package com.machpay.affiliate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.machpay.affiliate.common.enums.Gender;
import com.machpay.affiliate.common.enums.KYCStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Getter
@Setter
@ToString
public class Sender extends User {

    @Column
    @Type(type = "uuid-char")
    private UUID referenceId;

    @Column
    @Type(type = "uuid-char")
    private UUID oldReferenceId;

    @Column(nullable = false)
    private String firstName;

    @Column
    private String middleName;

    @Column(nullable = false)
    private String lastName;

    @Transient
    private String fullName;

    @Column(unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private boolean phoneNumberVerified;

    @Column(nullable = false)
    private boolean KYCVerified;

    @Column(length = 1000)
    private String imageUrl;

    @Column
    @Enumerated(EnumType.STRING)
    private KYCStatus kycStatus;

    @ManyToOne
    @JoinColumn(name = "state_id")
    @JsonIgnore
    private State state;

    @Transient
    private boolean newUser = false;

    @Column(nullable = false)
    private boolean deleted;

    @Column
    private Date deletedAt;

    @Column
    private Date accountDeletionRequestAt;

    @Column
    private String accountDeletionRevertReason;

    @Column(columnDefinition = "bit(1) DEFAULT 1")
    private boolean privacyPolicyAccepted;

    @Column(nullable = false)
    private boolean accountDeleteRequested;

    @Transient
    private String newPhoneNumber;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    private String dateOfBirth;

    public String getFullName() {
        return Stream
                .of(this.firstName, this.middleName, this.lastName)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(" "));
    }
}
