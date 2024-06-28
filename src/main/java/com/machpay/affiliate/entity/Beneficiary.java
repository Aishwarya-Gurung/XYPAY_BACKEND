package com.machpay.affiliate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Getter
@Setter
public class Beneficiary extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column
    private String middleName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private String email;

    @Column
    private String phoneNumber;

    @Column
    private String verificationStatus;

    @Column
    private String dateOfBirth;

    @Column
    private String senderRelationship;

    @Column
    @Type(type = "uuid-char")
    private UUID referenceId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Sender sender;

    @OneToOne(optional = false)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Column(nullable = false)
    private Boolean isCashPickupEnabled = false;

    @Column
    private boolean active = true;

    public String getFullName() {
        return Stream
                .of(this.firstName, this.middleName, this.lastName)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(" "));
    }
}
