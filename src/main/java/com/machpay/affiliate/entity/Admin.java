package com.machpay.affiliate.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Getter
@Setter
@ToString
public class Admin extends User {

    @Column(nullable = false)
    private String firstName;

    @Column
    private String middleName;

    @Column(nullable = false)
    private String lastName;

    @Transient
    private String fullName;

    @Column
    private String imageUrl;

    public String getFullName() {
        return Stream
                .of(this.firstName, this.middleName, this.lastName)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(" "));
    }
}
