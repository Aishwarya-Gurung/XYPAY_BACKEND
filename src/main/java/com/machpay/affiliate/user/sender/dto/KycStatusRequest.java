package com.machpay.affiliate.user.sender.dto;

import com.machpay.affiliate.common.enums.KYCStatus;
import lombok.Data;

@Data
public class KycStatusRequest {

    private KYCStatus status;
}
