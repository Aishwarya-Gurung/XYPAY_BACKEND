package com.machpay.affiliate.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;


@Entity
@Data
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String referenceId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String threeCharCode;

    @Column(nullable = false)
    private String twoCharCode;

    @Column
    private String phoneCode;

    @Column
    private String flagUrl;

    @OneToOne(optional = false)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;
}
