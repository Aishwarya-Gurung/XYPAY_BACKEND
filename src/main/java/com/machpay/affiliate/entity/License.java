package com.machpay.affiliate.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
@Getter
@Setter
public class License {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @Column
    private String licenseNo;

    @Column
    private String regulatoryBody;

    @Column
    private String regulatoryName;

    @Column
    private String regulatoryDivision;

    @Column
    private String address;

    @Column
    private String telephone;

    @Column
    private String fax;

    @Column
    private String website;

    @Column
    private String licenseType;

    @Column
    private String email;

    @Column(columnDefinition = "TEXT")
    private String stateDisclaimer;
}
