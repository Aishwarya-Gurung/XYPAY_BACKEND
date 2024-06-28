package com.machpay.affiliate.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum MSB {
    PRABHU("Prabhu"),
    GMT("GMT");

    private static final Map<String, MSB> msbHashMap = Arrays.stream(MSB.values())
            .collect(Collectors.toMap(MSB::getValue, Function.identity()));
    private String value;

    MSB(String msb) {
        this.value = msb;
    }

    public static MSB get(String value) {
        return msbHashMap.get(value);
    }
}
