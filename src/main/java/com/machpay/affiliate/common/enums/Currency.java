package com.machpay.affiliate.common.enums;

import lombok.Getter;

@Getter
public enum Currency {
    USD,
    SLE;

    public static boolean exists(String currencyCode) {
        for (Currency currency : Currency.values()) {
            if (currency.name().equalsIgnoreCase(currencyCode)) {
                return true;
            }
        }

        return false;
    }
}
