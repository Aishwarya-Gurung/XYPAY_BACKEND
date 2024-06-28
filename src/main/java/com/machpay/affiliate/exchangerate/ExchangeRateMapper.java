package com.machpay.affiliate.exchangerate;

import com.machpay.affiliate.entity.ExchangeRate;
import com.machpay.affiliate.exchangerate.dto.ExchangeRateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ExchangeRateMapper {

    @Named("toReferenceId")
    default UUID toReferenceId(String referenceId) {
        return UUID.fromString(referenceId);
    }

    ExchangeRateResponse toExchangeRateResponse(ExchangeRate exchangeRate);

    List<ExchangeRateResponse> toExchangeRateResponseList(List<ExchangeRate> exchangeRates);

}
