package com.machpay.affiliate.beneficiary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.machpay.affiliate.address.AddressResponse;
import com.machpay.affiliate.beneficiary.bank.dto.BeneficiaryBankResponse;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BeneficiaryResponse {

    private String firstName;

    private String middleName;

    private String lastName;

    private String fullName;

    private String email;

    private String senderRelationship;

    private String dateOfBirth;

    private AddressResponse address;

    private String phoneNumber;

    private UUID referenceId;

    private Boolean isCashPickupEnabled;

    @JsonProperty("isEditable")
    private boolean editable;

    @JsonProperty("isDeletable")
    private boolean deletable;

    private List<BeneficiaryBankResponse> banks;
}

