package com.machpay.affiliate.user.sender.dto;

import com.machpay.affiliate.common.enums.KYCStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusResponse {
    private Boolean isEmailVerified;

    private Boolean isPhoneNumberVerified;

    private Boolean isKYCVerified;

    private KYCStatus kycStatus;
}
