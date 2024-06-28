package com.machpay.affiliate.country.sourcedestinationcountry;

import com.machpay.affiliate.country.dto.CountryResponse;
import com.machpay.affiliate.entity.Country;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface SourceDestinationCountryMapper {
    @Mapping(target ="currency", ignore = true)
    @Mapping(target ="phoneCode", ignore = true)
    @Mapping(target ="twoCharCode", ignore = true)
    CountryResponse toSourceDestinationCountry(Country country);
}
