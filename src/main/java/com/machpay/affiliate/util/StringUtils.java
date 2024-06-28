package com.machpay.affiliate.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils extends org.springframework.util.StringUtils {

    public static String maskPhoneNumber(String phoneNumber) {
        return "xxx-xxx-xx".concat(phoneNumber.substring(phoneNumber.length() - 2));
    }

    public static String maskAccountNumber(String accountNumber) {
        return "**********".concat(accountNumber.substring(accountNumber.length() - 4));
    }
}
