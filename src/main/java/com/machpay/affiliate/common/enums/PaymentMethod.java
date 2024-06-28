package com.machpay.affiliate.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum PaymentMethod {
    DEBIT_CARD("Debit Card"),
    BANK_ACCOUNT("Bank Account");

    private static final Map<String, PaymentMethod> paymentMethodHashMap = Arrays.stream(PaymentMethod.values())
            .collect(Collectors.toMap(PaymentMethod::getValue, Function.identity()));
    private String value;

    PaymentMethod(String provider) {
        this.value = provider;
    }

    public static PaymentMethod get(String value) {
        return paymentMethodHashMap.get(value);
    }
}
