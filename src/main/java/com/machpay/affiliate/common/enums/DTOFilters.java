package com.machpay.affiliate.common.enums;

import lombok.Getter;

@Getter
public enum DTOFilters {
    SENDER_RESPONSE("senderFilter");

    private final String value;

    DTOFilters(String value) {
        this.value = value;
    }
}
