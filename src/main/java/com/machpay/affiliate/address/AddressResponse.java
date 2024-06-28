package com.machpay.affiliate.address;

import lombok.Data;

@Data
public class AddressResponse {
    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String postalCode;

    private String country;
}
