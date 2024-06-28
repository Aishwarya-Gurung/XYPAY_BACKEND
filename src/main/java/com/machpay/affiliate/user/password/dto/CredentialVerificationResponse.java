package com.machpay.affiliate.user.password.dto;

import com.machpay.affiliate.common.enums.CredentialVerificationStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CredentialVerificationResponse {

    private CredentialVerificationStatus status;

    public CredentialVerificationResponse(CredentialVerificationStatus status) {
        this.status = status;
    }
}
