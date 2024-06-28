package com.machpay.affiliate.device.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class DeviceVerificationRequest {

    @NotBlank
    @Size(max = 6)
    private String device;
}
