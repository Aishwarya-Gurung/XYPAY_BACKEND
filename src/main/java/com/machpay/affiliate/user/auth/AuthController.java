package com.machpay.affiliate.user.auth;

import com.machpay.affiliate.entity.Sender;
import com.machpay.affiliate.security.CurrentUser;
import com.machpay.affiliate.redis.AuthTokenService;
import com.machpay.affiliate.security.UserPrincipal;
import com.machpay.affiliate.common.enums.DeviceType;
import com.machpay.affiliate.user.auth.dto.SignUpRequestV2;
import com.machpay.affiliate.user.permission.PermissionService;
import com.machpay.affiliate.user.sender.SenderService;
import com.machpay.affiliate.user.auth.dto.AuthResponse;
import com.machpay.affiliate.user.auth.dto.LoginRequest;
import com.machpay.affiliate.user.auth.dto.SignUpRequest;
import com.machpay.affiliate.user.password.PasswordService;
import com.machpay.affiliate.user.auth.dto.AccessTokenRequest;
import com.machpay.affiliate.common.annotations.VerifiedDevice;
import com.machpay.affiliate.user.auth.dto.AccessTokenResponse;
import com.machpay.affiliate.user.auth.dto.Oauth2SignupRequest;
import com.machpay.affiliate.user.password.ResetPasswordRequest;
import com.machpay.affiliate.user.sender.dto.SenderAuthResponse;
import com.machpay.affiliate.user.password.ForgotPasswordRequest;
import com.machpay.affiliate.user.auth.dto.ForgetPasswordResponse;
import com.machpay.affiliate.user.verification.ContactVerificationService;
import com.machpay.affiliate.user.verification.VerificationResponse;
import com.machpay.affiliate.user.verification.TwoFaVerificationService;
import com.machpay.affiliate.user.password.dto.CredentialVerificationRequest;
import com.machpay.affiliate.user.password.dto.CredentialVerificationResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private SenderService senderService;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private TwoFaVerificationService twoFaVerificationService;

    @Autowired
    private ContactVerificationService contactVerificationService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private PermissionService permissionService;

    // Note: This endpoint is not invoked during sign in since we have made custom authentication filter
    // SecurityConfig.java to handle success and failure of authentication
    @PostMapping("/signin")
    public AuthResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    ////////
    @PostMapping("/users/create")
    public AuthResponse create(@Valid @RequestBody SignUpRequestV2 signUpRequestV2, HttpServletRequest request) {
        return authService.signUpV2(signUpRequestV2, request);
    }

    ////////
    @PostMapping("/users/{type}/{contact}")
    public void sendContactVerificationCode(@PathVariable("type") DeviceType deviceType, @PathVariable String contact) {
        contactVerificationService.sendVerificationCode(deviceType, contact);
    }

    ////////
    @GetMapping("/verify/{type}/{contact}/otp/{token}")
    public void verifyContact(@PathVariable("type") DeviceType deviceType, @PathVariable String contact, @PathVariable String token) {
        contactVerificationService.verify(deviceType, contact, token);
    }

    @VerifiedDevice
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/users/verify/{type}/{contact}/otp/{token}")
    public void verifySenderContact(@CurrentUser UserPrincipal userPrincipal, @PathVariable("type") DeviceType deviceType,
                                    @PathVariable("contact") String contact, @PathVariable("token") String token) {
        contactVerificationService.verifySenderContact(userPrincipal.getId(), deviceType, contact, token);
    }

    @VerifiedDevice
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/verify/{type}/{token}")
    public void verify(@CurrentUser UserPrincipal userPrincipal,
                       @PathVariable("type") DeviceType deviceType, @PathVariable("token") String token) {
        twoFaVerificationService.verifyToken(userPrincipal.getId(), token, deviceType);
    }

    @VerifiedDevice
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/resend-verification/{type}")
    public void resendVerification(@CurrentUser UserPrincipal userPrincipal,
                                   @PathVariable("type") DeviceType deviceType) {
        Sender sender = senderService.findById(userPrincipal.getId());
        twoFaVerificationService.createDeviceVerification(sender, deviceType);
    }

    @GetMapping("/guest")
    @PreAuthorize("hasRole('READ')")
    public SenderAuthResponse getGuestInfo(@CurrentUser UserPrincipal userPrincipal) {
        return authService.getGuestInfo(userPrincipal.getId());
    }

    @GetMapping("/signout")
    @PreAuthorize("hasRole('READ')")
    public void logOut(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null) {
            String referenceToken = authHeader.replace("Bearer", "").trim();
            authTokenService.deleteAuthTokenByReferenceToken(referenceToken);
        }
    }

    @PostMapping("/forgot-password")
    public ForgetPasswordResponse forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        return passwordService.handleForgetPassword(forgotPasswordRequest.getEmail());
    }

    @PutMapping("/reset-password/{recoveryToken}")
    public VerificationResponse resetPassword(@PathVariable String recoveryToken,
                                              @RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        return passwordService.resetPassword(recoveryToken, resetPasswordRequest.getNewPassword());
    }

    @PutMapping("/reset-password")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public VerificationResponse resetLoggedInUserPassword(@CurrentUser UserPrincipal userPrincipal,
                                                          @RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        return passwordService.resetPassword(userPrincipal, resetPasswordRequest.getNewPassword(),
                resetPasswordRequest.getOldPassword());
    }

    @PostMapping("/token")
    public AccessTokenResponse refreshAccessToken(@Valid @RequestBody AccessTokenRequest accessTokenRequest) {
        return authService.refreshAccessToken(accessTokenRequest);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/verify-credentials")
    public CredentialVerificationResponse verifyCredentials(@CurrentUser UserPrincipal userPrincipal,
                                                            @RequestBody @Valid CredentialVerificationRequest credentialVerificationRequest) {
        return authService.isCredentialsValid(userPrincipal.getId(), credentialVerificationRequest);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/permissions")
    public void defaultPermission() {
        permissionService.defaultPermissionForOldUser();
    }
}