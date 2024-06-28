package com.machpay.affiliate.country.currency;

import com.machpay.affiliate.country.dto.CurrencyResponse;
import com.machpay.affiliate.entity.Country;
import com.machpay.affiliate.entity.Currency;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CurrencyMapper {

    CurrencyResponse toCurrencyResponse(Currency currency);

    @IterableMapping(qualifiedByName = "toCurrencyList")
    List<Currency> toCurrencyList(List<Country> countries);
}
