package com.machpay.affiliate.country.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayoutMethodResponse {

    @JsonProperty("isBankDepositEnabled")
    private boolean bankDepositEnabled;

    @JsonProperty("isCashPickupEnabled")
    private boolean cashPickupEnabled;

    @JsonProperty("isHomeDeliveryEnabled")
    private boolean homeDeliveryEnabled;

    @JsonProperty("isMobileWalletEnabled")
    private boolean mobileWalletEnabled;
}
