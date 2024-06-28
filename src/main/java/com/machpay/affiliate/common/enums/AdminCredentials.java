package com.machpay.affiliate.common.enums;

import lombok.Getter;

@Getter
public enum AdminCredentials {
    FIRST_NAME("Super"),
    LAST_NAME("Admin"),
    PASSWORD("L6[e66UC/pS-x/!f"),
    EMAIL("admin@xypay.com");

    private String value;

    AdminCredentials(String value) {
        this.value = value;
    }
}
