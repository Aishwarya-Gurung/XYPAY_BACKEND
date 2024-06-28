package com.machpay.affiliate.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String referenceId;

    @Column
    private String code;

    @Column
    private String name;

    @Column
    private String symbol;
}
