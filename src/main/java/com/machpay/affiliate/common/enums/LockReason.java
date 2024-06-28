package com.machpay.affiliate.common.enums;

import lombok.Getter;

@Getter
public enum LockReason {
    MAX_LOGIN_LIMIT_EXCEEDED("Login attempt limit exceeded"),
    SPAM_ACCOUNT("Spam account. User is locked by Admin."),
    DEVICE_VERIFICATION_CODE_RESEND_LIMIT_EXCEEDED("Device verification code resend limit exceeded"),
    DEVICE_VERIFICATION_ATTEMPT_LIMIT_EXCEEDED("Device verification attempt limit exceeded"),
    PHONE_VERIFICATION_CODE_RESEND_LIMIT_EXCEEDED("Phone verification code resend limit exceeded"),
    PHONE_VERIFICATION_ATTEMPT_LIMIT_EXCEEDED("Phone verification attempt limit exceeded"),
    EMAIL_VERIFICATION_CODE_RESEND_LIMIT_EXCEEDED("Email verification code resend limit exceeded"),
    EMAIL_VERIFICATION_ATTEMPT_LIMIT_EXCEEDED("Email verification attempt limit exceeded");

    private String value;

    LockReason(String value) {
        this.value = value;
    }
}
