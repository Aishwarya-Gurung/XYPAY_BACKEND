package com.machpay.affiliate.device;

import com.machpay.affiliate.common.ListResponse;
import com.machpay.affiliate.common.annotations.VerifiedEmail;
import com.machpay.affiliate.common.annotations.VerifiedPhone;
import com.machpay.affiliate.common.annotations.VerifiedDevice;
import com.machpay.affiliate.device.dto.DeviceResponse;
import com.machpay.affiliate.device.dto.DeviceVerificationRequest;
import com.machpay.affiliate.entity.User;
import com.machpay.affiliate.security.CurrentUser;
import com.machpay.affiliate.security.UserPrincipal;
import com.machpay.affiliate.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1")
public class DeviceController {

    @Autowired
    private UserService userService;

    @Autowired
    private DeviceService deviceService;

    @VerifiedDevice
    @GetMapping("/devices")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ListResponse getAllDevices(@CurrentUser UserPrincipal userPrincipal,
                                      @RequestHeader("Authorization") String authHeader) {
        User user = userService.findById(userPrincipal.getId());
        String referenceToken = authHeader.replace("Bearer", "").trim();
        List<DeviceResponse> deviceResponse = deviceService.getDeviceList(user, referenceToken);

        return new ListResponse(deviceResponse);
    }

    @PostMapping("/devices/{deviceId}/verify")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public String verifyDevice(@CurrentUser UserPrincipal userPrincipal,
                               @Valid @RequestBody DeviceVerificationRequest deviceVerificationRequest,
                               @PathVariable String deviceId,
                               @RequestHeader("Authorization") String authHeader) {
        User user = userService.findById(userPrincipal.getId());
        String referenceToken = authHeader.replace("Bearer", "").trim();
        deviceService.verifyDevice(user, deviceVerificationRequest, deviceId, referenceToken);

        return "Device verified successfully.";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PostMapping("/devices/{deviceId}/resend-verification-code")
    public String resendVerificationCode(@CurrentUser UserPrincipal userPrincipal,
                                         @PathVariable String deviceId) {
        User user = userService.findById(userPrincipal.getId());
        deviceService.resendVerificationCode(user, deviceId);

        return "Device verification code is sent to your email account successfully.";
    }

    @VerifiedDevice
    @DeleteMapping("devices/{deviceId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public String removeDevice(@CurrentUser UserPrincipal userPrincipal, @PathVariable String deviceId,
                               HttpServletRequest request) {
        User user = userService.findById(userPrincipal.getId());
        deviceService.removeDevice(user, deviceId, request);

        return "Device removed successfully.";
    }
}