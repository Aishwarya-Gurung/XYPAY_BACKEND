package com.machpay.affiliate.common;

import com.google.common.collect.ImmutableMap;
import com.machpay.affiliate.common.enums.AffiliateCountry;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public final class Constants {
    public static final int MAX_LOGIN_LIMIT = 5;
    public static final int RESEND_VERIFICATION_CODE_LIMIT = 5;
    public static final String DEFAULT_PHONE_VERIFICATION_CODE = "123456";
    public static final String COUNTRY = "Country";
    public static final String RESET_PASS = "Reset Password for XYPAY Account";
    public static final String VERIFY_EMAIL_ID = "Activate your XYPAY account";
    public static final String VERIFY_TRANSACTION = "Verify your XYPAY transaction";
    public static final String PARSE_ERROR = "Something went wrong while parsing /login request body";

    public static final int RESEND_DEVICE_VERIFICATION_CODE_LIMIT = 5;
    public static final String RESEND_DEVICE_VERIFICATION_CODE_LIMIT_EXCEEDED= "Resending device verification code limit exceeded.";
    public static final String DEFAULT_DEVICE_VERIFICATION_CODE = "162534";
    public static final String PHONE_NUMBER_VERIFICATION_SMS = " is your verification code";
    public static final long INACTIVE_ACCESS_TOKEN_DELETE_TIME = 15;

    private static final List<String> SIERRA_LEONE_PAYOUT_CURRENCY = new ArrayList<>(Collections.singletonList("SLE"));

    public static final ImmutableMap<AffiliateCountry, List<String>> CORRIDOR_WISE_PAYOUT_CURRENCY = ImmutableMap.of(
            AffiliateCountry.SIERRA_LEONE, SIERRA_LEONE_PAYOUT_CURRENCY
    );

    public static final int MAX_OTP_VERIFICATION_ATTEMPT = 5;

    private Constants() {
        throw new IllegalStateException("Constant class");
    }
}

