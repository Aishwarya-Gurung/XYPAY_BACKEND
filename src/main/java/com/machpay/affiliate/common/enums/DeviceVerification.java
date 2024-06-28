package com.machpay.affiliate.common.enums;

import lombok.Getter;

@Getter
public enum DeviceVerification {
    VERIFICATION_REQUIRED("device_verification_required");

    private String value;

    DeviceVerification(String value) {
        this.value = value;
    }
}
