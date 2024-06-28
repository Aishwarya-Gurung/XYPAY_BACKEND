package com.machpay.affiliate.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum PayoutMethod {
    CASH_PICKUP("Cash Pickup"),
    BANK_DEPOSIT("Bank Deposit"),
    HOME_DELIVERY("Home Delivery"),
    WALLET("Wallet Payment");

    private static final Map<String, PayoutMethod> payoutMethodHashMap = Arrays.stream(PayoutMethod.values())
            .collect(Collectors.toMap(PayoutMethod::getValue, Function.identity()));
    private String value;

    PayoutMethod(String provider) {
        this.value = provider;
    }

    public static PayoutMethod get(String value) {
        return payoutMethodHashMap.get(value);
    }
}
