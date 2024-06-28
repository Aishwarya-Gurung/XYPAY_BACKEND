package com.machpay.affiliate.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
public class ExchangeRate extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Type(type = "uuid-char")
    private UUID referenceId;

    @Column
    private BigDecimal rate;

    @Column
    private String sourceCurrency;

    @Column
    private String sourceCurrencyName;

    @Column
    private String destinationCurrency;

    @Column
    private String destinationCurrencyName;

    @Column
    private Date activeFrom;

    @Column
    private Date expiredAt;
}
