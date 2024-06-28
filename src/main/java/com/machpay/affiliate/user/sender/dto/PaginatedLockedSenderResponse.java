package com.machpay.affiliate.user.sender.dto;

import com.machpay.affiliate.common.Paging;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PaginatedLockedSenderResponse {
    private List<SenderResponse> senderResponseList;

    private Paging paging;
}
