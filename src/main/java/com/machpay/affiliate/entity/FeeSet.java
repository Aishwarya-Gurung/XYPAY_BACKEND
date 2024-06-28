package com.machpay.affiliate.entity;

import com.machpay.affiliate.common.enums.PaymentMethod;
import com.machpay.affiliate.common.enums.PayoutMethod;
import lombok.Data;
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
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
public class FeeSet extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Type(type = "uuid-char")
    private UUID referenceId;

    @Column
    private BigDecimal minAmount;

    @Column
    private BigDecimal maxAmount;

    @Column
    private BigDecimal feeAmount;

    @Column
    private Date activeFrom;

    @Column
    private Date expiredAt;

    @Column
    private PayoutMethod payoutMethod;

    @Column
    private PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_destination_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SourceDestinationCountry sourceDestinationCountry;
}
