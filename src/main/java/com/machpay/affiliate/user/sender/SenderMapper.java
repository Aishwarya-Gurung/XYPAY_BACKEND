package com.machpay.affiliate.user.sender;

import com.machpay.affiliate.entity.Sender;
import com.machpay.affiliate.user.auth.dto.SignUpRequest;
import com.machpay.affiliate.user.auth.dto.SignUpRequestV2;
import com.machpay.affiliate.user.sender.dto.AddressResponse;
import com.machpay.affiliate.user.sender.dto.BasicInfoResponse;
import com.machpay.affiliate.user.sender.dto.DeletionRequestedSenderResponse;
import com.machpay.affiliate.user.sender.dto.KYCInfo;
import com.machpay.affiliate.user.sender.dto.SenderAuthResponse;
import com.machpay.affiliate.user.sender.dto.SenderResponse;
import com.machpay.affiliate.user.sender.dto.StatusResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface SenderMapper {
    SenderAuthResponse toUserResponse(Sender sender);

    @Mapping(target = "roles", ignore = true)
    BasicInfoResponse toBasicInfoResponse(Sender sender);

    @Mapping(target = "isKYCVerified", source = "KYCVerified")
    @Mapping(target = "isEmailVerified", source = "emailVerified")
    @Mapping(target = "isPhoneNumberVerified", source = "phoneNumberVerified")
    StatusResponse toStatusResponse(Sender sender);

    @Mapping(source = "sender.state.code", target = "stateCode")
    @Mapping(source = "sender.state.name", target = "state")
    @Mapping(source = "sender.state.country.name", target = "country")
    AddressResponse toAddressResponse(Sender sender);

    @Mapping(target = "state", ignore = true)
    @Mapping(target = "phoneNumber", expression = "java(signUpRequest.getCountryCode().concat(signUpRequest" +
            ".getPhoneNumber()).trim())")
    Sender toSender(SignUpRequest signUpRequest);

    @Mapping(target = "state", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "phoneNumber", expression = "java(signUpRequestV2.getCountryCode().concat(signUpRequestV2" +
            ".getPhoneNumber()).trim())")
    Sender toSender(SignUpRequestV2 signUpRequestV2);

    @Mapping(target = "lockedReason", expression = "java(sender.getLockReason().getValue())")
    SenderResponse toSenderResponse(Sender sender);

    List<SenderResponse> toSenderResponseList(List<Sender> senders);

    DeletionRequestedSenderResponse toDeletionRequestedSenderResponse(Sender sender);

    List<DeletionRequestedSenderResponse> toDeletionRequestedSenderResponseList(List<Sender> senders);

    Sender toSender(@MappingTarget Sender sender, String firstName, String middleName, String lastName);

    KYCInfo toKYCInfo(KYCInfo kycInfo);
}