package com.machpay.affiliate.user.sender.dto;

import com.machpay.affiliate.senderAddress.SenderAddressResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class BasicInfoResponse {
    private Long id;

    private UUID referenceId;

    private String firstName;

    private String middleName;

    private String lastName;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String imageUrl;

    private List<String> roles;

    private AddressResponse address;

    private String gender;

    private String dateOfBirth;

    private SenderAddressResponse senderAddress;
}