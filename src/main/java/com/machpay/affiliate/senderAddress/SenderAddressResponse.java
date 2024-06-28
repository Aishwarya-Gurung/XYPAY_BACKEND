package com.machpay.affiliate.senderAddress;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SenderAddressResponse {
    private String addressLine1;

    private String addressLine2;

    private String city;

    private String zipcode;
}
