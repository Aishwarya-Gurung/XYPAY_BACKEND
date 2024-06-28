package com.machpay.affiliate.common.enums;

import lombok.Getter;

@Getter
public enum TransactionHoldReason {
    OTHERS("Others");

    private final String value;

    TransactionHoldReason(String value) {
        this.value = value;
    }
}
