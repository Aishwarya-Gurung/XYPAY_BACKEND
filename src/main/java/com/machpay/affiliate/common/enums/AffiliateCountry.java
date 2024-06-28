package com.machpay.affiliate.common.enums;

import lombok.Getter;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum AffiliateCountry {
    SIERRA_LEONE("SLE");

    private final String threeCharCode;

    private static final Map<String, AffiliateCountry> affiliateCountryHashMap =
            Arrays.stream(AffiliateCountry.values())
                    .collect(Collectors.toMap(AffiliateCountry::getThreeCharCode, Function.identity()));

    public static AffiliateCountry get(String threeCharCode) {
        return affiliateCountryHashMap.get(threeCharCode);
    }

    AffiliateCountry(String threeCharCode) {
        this.threeCharCode = threeCharCode;
    }

    public static List<String> getAllNames() {
        return Stream.of(AffiliateCountry.values()).map(AffiliateCountry::name).collect(Collectors.toList());
    }

    public static List<String> getAllThreeCharCodeCode() {
        return Stream.of(AffiliateCountry.values()).map(AffiliateCountry::getThreeCharCode).collect(Collectors.toList());
    }
}
