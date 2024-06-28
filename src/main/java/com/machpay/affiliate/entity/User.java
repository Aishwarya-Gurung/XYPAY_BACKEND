package com.machpay.affiliate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.machpay.affiliate.common.enums.LockReason;
import com.machpay.affiliate.common.enums.AuthProvider;
import com.machpay.affiliate.common.enums.RoleType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean emailVerified;

    @Column
    private String refreshToken;

    @Column
    private Integer refreshTokenUsed;

    @JsonIgnore
    private String password;

    @Column
    private boolean passwordExpired;

    @Column
    private int loginAttempts;

    @Column
    private boolean locked;

    @Column
    @Enumerated(EnumType.STRING)
    private LockReason lockReason;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Column
    private String providerId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    public boolean isAdmin() {
        Collection<Role> roles = this.getRoles();

        for (Role role : roles) {
            if (role.getName().equals(RoleType.ROLE_ADMIN)) {
                return true;
            }
        }

        return false;
    }
}