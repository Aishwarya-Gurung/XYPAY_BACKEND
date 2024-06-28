package com.machpay.affiliate.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum DeviceType {
    PHONE("phone"),
    EMAIL("email");

    private static final Map<String, DeviceType> deviceTypeHashMap = Arrays.stream(DeviceType.values())
            .collect(Collectors.toMap(DeviceType::getType, Function.identity()));
    private String type;

    DeviceType(String type) {
        this.type = type;
    }

    public static DeviceType get(String value) {
        return deviceTypeHashMap.get(value);
    }
}
