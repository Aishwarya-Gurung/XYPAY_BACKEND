package com.machpay.affiliate.entity;

import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
@Data
public class FeeRange extends AuditModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private BigDecimal minAmount;

    @Column
    private BigDecimal maxAmount;

    @Column
    private BigDecimal flatFee;

    @Column
    private BigDecimal percentageFee;

    @Column
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fee_parameter_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FeeParameter feeParameter;
}

