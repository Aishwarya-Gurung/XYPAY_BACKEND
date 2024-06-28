package com.machpay.affiliate.fee.mapper;

import com.machpay.affiliate.entity.Country;
import com.machpay.affiliate.entity.FeeParameter;
import com.machpay.affiliate.entity.FeeRange;
import com.machpay.affiliate.entity.FeeSet;
import com.machpay.affiliate.fee.dto.FeeParameterResponse;
import com.machpay.affiliate.fee.dto.FeeRangeRequest;
import com.machpay.affiliate.fee.dto.FeeSetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface FeeMapper {

    @Named("toReferenceId")
    default UUID toReferenceId(String referenceId) {
        return UUID.fromString(referenceId);
    }

    FeeSetResponse toFeeSetResponse(FeeSet feeSet);

    List<FeeSetResponse> toFeeSetResponseList(List<FeeSet> feeSets);

    @Mapping(target = "currency", ignore = true)
    @Mapping(target = "sourceDestinationCountry", ignore = true)
    FeeParameterResponse toFeeParameterResponse(FeeParameter feeParameter);

    FeeRange toFeeRange(FeeRangeRequest feeRangeRequest);

    FeeSetResponse.Country toFeeSetCountryResponse(Country country);
}
