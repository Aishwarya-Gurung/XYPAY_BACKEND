package com.machpay.affiliate.user.sender.dto;

import com.machpay.affiliate.common.enums.LockReason;
import lombok.Getter;

import java.util.List;

@Getter
public class LockedSenderFilterRequest {
    private String email;

    private List<LockReason> lockedReasons;
}
