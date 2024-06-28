package com.machpay.affiliate.address;

import com.machpay.affiliate.beneficiary.dto.BeneficiaryRequest;
import com.machpay.affiliate.entity.Address;
import com.machpay.affiliate.entity.Beneficiary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface AddressMapper {

    Address toAddress(BeneficiaryRequest beneficiaryInfo);


    @Mapping(source = "beneficiary.address.addressLine1", target = "addressLine1")
    @Mapping(source = "beneficiary.address.addressLine2", target = "addressLine2")
    @Mapping(source = "beneficiary.address.country", target = "country")
    @Mapping(source = "beneficiary.address.city", target = "city")
    @Mapping(source = "beneficiary.address.postalCode", target = "postalCode")
    @Mapping(source = "beneficiary.address.state", target = "state")
    AddressResponse toAddressResponse(Beneficiary beneficiary);
}
