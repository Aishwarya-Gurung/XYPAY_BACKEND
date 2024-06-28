package com.machpay.affiliate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.machpay.affiliate.common.enums.MSB;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class State {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String referenceId;

    @Column
    private String name;

    @Column
    private String code;

    @Column(columnDefinition = "bit(1) DEFAULT 1")
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Country country;

    @OneToOne(mappedBy = "state")
    private License license;

    @Column
    @Enumerated(EnumType.STRING)
    private MSB msb;
}
