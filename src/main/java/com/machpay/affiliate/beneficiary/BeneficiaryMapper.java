package com.machpay.affiliate.beneficiary;

import com.machpay.affiliate.beneficiary.dto.BeneficiaryRequest;
import com.machpay.affiliate.beneficiary.dto.BeneficiaryResponse;
import com.machpay.affiliate.entity.Beneficiary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface BeneficiaryMapper {

    @Mapping(target = "referenceId", ignore = true)
    Beneficiary toBeneficiary(BeneficiaryRequest beneficiaryInfo);

    @Mapping(target = "referenceId", ignore = true)
    Beneficiary toBeneficiary(BeneficiaryRequest beneficiaryRequest, @MappingTarget Beneficiary beneficiary);

    @Mapping(target = "address", source = "beneficiary.address")
    @Mapping(target = "fullName", expression = "java(BeneficiaryService.createFullName(beneficiary))")
    BeneficiaryResponse toBeneficiaryResponse(Beneficiary beneficiary);

    BeneficiaryRequest toBeneficiaryRequest(Beneficiary beneficiary);
}
