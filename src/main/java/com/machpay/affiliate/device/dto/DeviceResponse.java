package com.machpay.affiliate.device.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeviceResponse {

    private UUID id;

    private String ip;

    private String os;

    private String browser;

    private String browserVersion;

    private String deviceType;

    @JsonIgnore
    private String fingerprint;

    private String lastLoginDate;

    private boolean currentDevice;
}
