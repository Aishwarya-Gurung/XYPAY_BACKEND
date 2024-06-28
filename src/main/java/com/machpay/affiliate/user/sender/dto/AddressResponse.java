package com.machpay.affiliate.user.sender.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponse {
    private String addressLine1;

    private String stateCode;

    private String state;

    private String country;
}
