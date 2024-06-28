package com.machpay.affiliate.user.sender.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SenderResponse {
    private Long id;

    private UUID referenceId;

    private String firstName;

    private String middleName;

    private String lastName;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String imageUrl;

    private String lockedReason;
}
