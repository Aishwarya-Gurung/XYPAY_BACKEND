package com.machpay.affiliate.common.enums;

public enum KYCStatus {
    RETRY,
    VERIFIED,
    DOCUMENT,
    SUSPENDED,
    UNVERIFIED,
    IN_PROGRESS,
    REVIEW_PENDING,
    RETRY_REQUESTED,
    DOCUMENT_REQUESTED,
    SUBMITTED;

    public static Boolean isKycVerifiable(KYCStatus status) {
        return status.equals(KYCStatus.VERIFIED);
    }
}
