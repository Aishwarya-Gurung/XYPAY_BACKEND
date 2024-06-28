package com.machpay.affiliate.user.sender.dto;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.machpay.affiliate.common.enums.AuthProvider;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonFilter("senderFilter")
public class SenderAuthResponse {
    private BasicInfoResponse sender;

    private StatusResponse status;

    private AuthProvider provider;

    @JsonProperty("isPrivacyPolicyAccepted")
    private boolean privacyPolicyAccepted;

    @JsonProperty("isAccountDeleteRequested")
    private boolean accountDeleteRequested;
}