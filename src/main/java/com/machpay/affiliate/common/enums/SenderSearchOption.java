package com.machpay.affiliate.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum SenderSearchOption {
    EMAIL("email"),
    LOCK_REASON("lock_reason");

    private String value;

    SenderSearchOption(String value) {
        this.value = value;
    }

    private static final Map<String, SenderSearchOption> searchOptionHashMap = Arrays.stream(SenderSearchOption.values())
            .collect(Collectors.toMap(SenderSearchOption::getValue, Function.identity()));

    public static SenderSearchOption get(String value) {
        return searchOptionHashMap.get(value);
    }
}
