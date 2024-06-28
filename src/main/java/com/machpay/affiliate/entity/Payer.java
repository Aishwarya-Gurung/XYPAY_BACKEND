package com.machpay.affiliate.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Payer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long referenceId;

    @Column
    private String name;

    @Column
    private String receivingCurrency;

    @Column
    private String code;

    @Column
    private String country;

    @Column
    private String address;

    @Column
    private String phoneNumber;
}
