package com.machpay.affiliate.user.auth;

import com.machpay.affiliate.entity.Sender;
import com.machpay.affiliate.user.auth.dto.GuestInfoResponse;
import com.machpay.affiliate.user.auth.dto.SignUpRequest;
import com.machpay.affiliate.user.sender.dto.AddressResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface AuthMapper {

    @Mapping(source = "sender.state.code", target = "stateCode")
    @Mapping(source = "sender.state.name", target = "state")
    @Mapping(source = "sender.state.country.name", target = "country")
    AddressResponse toAddressResponse(Sender sender);

    GuestInfoResponse toGuestInfoResponse(Sender sender);

    @Mapping(target = "state", ignore = true)
    SignUpRequest toSignUpRequest(Sender sender);
}