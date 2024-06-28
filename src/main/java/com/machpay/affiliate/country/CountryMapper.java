package com.machpay.affiliate.country;

import com.machpay.affiliate.country.currency.CurrencyMapper;
import com.machpay.affiliate.country.dto.CountryResponse;
import com.machpay.affiliate.entity.Country;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {CurrencyMapper.class})
public interface CountryMapper {

    @Mapping(target = "currency", ignore = true)
    CountryResponse toCountryResponse(Country country);

    @IterableMapping(qualifiedByName = "toCountryResponseList")
    List<CountryResponse> toCountryResponseList(List<Country> destinationCountry);

}
