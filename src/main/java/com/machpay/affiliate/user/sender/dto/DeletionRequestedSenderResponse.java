package com.machpay.affiliate.user.sender.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class DeletionRequestedSenderResponse {
    private Long id;

    private UUID referenceId;

    private String firstName;

    private String middleName;

    private String lastName;

    private String fullName;

    private String email;

    private String phoneNumber;

    private Date accountDeletionRequestAt;

    private String imageUrl;
}
