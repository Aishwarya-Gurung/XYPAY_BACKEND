package com.machpay.affiliate.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum PrabhuStates {
    Michigan("MI"),
    Pennsylvania("PA");

    private String twoCharCode;

    PrabhuStates(String twoCharCode) {
        this.twoCharCode = twoCharCode;
    }

    private static final Map<String, PrabhuStates> prabhuStatesHashMap = Arrays.stream(PrabhuStates.values())
            .collect(Collectors.toMap(PrabhuStates::getTwoCharCode, Function.identity()));


    public static MSB get(String state) {
        return PrabhuStates.get(state);
    }

    public static List<String> getPrabhuStates() {
        return Stream.of(PrabhuStates.values()).map(PrabhuStates::getTwoCharCode).collect(Collectors.toList());
    }
}
