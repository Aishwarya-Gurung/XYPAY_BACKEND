package com.machpay.affiliate.user.sender.dto;

import com.machpay.affiliate.common.enums.SenderSearchOption;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterLockedSenderRequest {

    private Integer pageSize;

    private Integer page;

    private SenderSearchOption searchOption;

    private LockedSenderFilterRequest lockedSenderFilterRequest;
}
