package com.machpay.affiliate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class BeneficiaryBank extends AuditModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String referenceId;

    @Column
    private String accountNumber;

    @Column
    private String accountType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bank_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Bank bank;

    @ManyToOne(optional = false)
    @JoinColumn(name = "beneficiary_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Beneficiary beneficiary;
}
