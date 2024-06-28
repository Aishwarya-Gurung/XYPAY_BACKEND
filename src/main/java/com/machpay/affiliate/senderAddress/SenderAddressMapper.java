package com.machpay.affiliate.senderAddress;

import com.machpay.affiliate.entity.Sender;
import com.machpay.affiliate.entity.SenderAddress;
import com.machpay.affiliate.user.auth.dto.SignUpRequestV2;
import com.machpay.affiliate.user.sender.dto.KYCInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface SenderAddressMapper {

    com.machpay.affiliate.senderAddress.SenderAddressResponse toSenderAddressResponse(SenderAddress senderAddress);

    @Mapping(source = "sender", target = "sender")
    SenderAddress toSenderAddress(SignUpRequestV2 signUpRequestV2, Sender sender);

    @Mapping(source = "sender", target = "sender")
    SenderAddress toSenderAddress(KYCInfo kycInfo, Sender sender);

}
