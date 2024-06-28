package com.machpay.affiliate.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
public class Address implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String addressLine1;

    @Column
    private String addressLine2;

    @Column
    private String city;

    @Column
    private String state;

    @Column
    private String postalCode;

    @Column
    private String country;

    @OneToOne(mappedBy = "address")
    private Beneficiary beneficiary;
}
