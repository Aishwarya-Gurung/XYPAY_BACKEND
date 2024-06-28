package com.machpay.affiliate.country.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourceDestinationCountryResponse {

    private Long id;

    private CountryResponse source;

    private CountryResponse destination;
}
