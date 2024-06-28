package com.machpay.affiliate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

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
public class CashPickupLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String branchName;

    @Column
    private String address;

    @Column
    private String province;

    @Column
    private String type;

    @Column
    private String agent;

    @ManyToOne
    @JoinColumn(name = "payer_id")
    @JsonIgnore
    private Payer payer;

    @Column
    private boolean active;
}
