package com.machpay.affiliate.state.license.dto;

import com.machpay.affiliate.common.enums.MSB;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LicenseResponse {
    private Long id;

    private String licenseNo;

    private String regulatoryBody;

    private String regulatoryName;

    private String regulatoryDivision;

    private String address;

    private String telephone;

    private String fax;

    private String website;

    private String licenseType;

    private String email;

    private String stateDisclaimer;

    private MSB msb;
}
