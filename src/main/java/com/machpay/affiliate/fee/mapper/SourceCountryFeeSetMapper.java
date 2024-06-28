package com.machpay.affiliate.fee.mapper;

import com.machpay.affiliate.entity.FeeSet;
import com.machpay.affiliate.fee.dto.SourceCountryFeeSetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {FeeMapper.class})
public interface SourceCountryFeeSetMapper {
    @Mapping(source = "feeSet.sourceDestinationCountry.destinationCountry.threeCharCode", target = "destinationCountryCode")
    @Mapping(source = "feeSet", target = "feeSet")
    @Mapping(source = "payoutMethod", target = "payoutMethod")
    @Mapping(source = "paymentMethod", target = "paymentMethod")
    SourceCountryFeeSetResponse toSourceCountryFeeSetRespone(FeeSet feeSet);

    List<SourceCountryFeeSetResponse> toSourceCountryFeeSetResponeList(List<FeeSet> feeSets);
}
