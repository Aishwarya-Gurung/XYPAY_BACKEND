package com.machpay.affiliate.user.auth;

import com.machpay.affiliate.common.Messages;
import com.machpay.affiliate.common.enums.CredentialVerificationStatus;
import com.machpay.affiliate.common.enums.DeviceType;
import com.machpay.affiliate.common.enums.DeviceVerification;
import com.machpay.affiliate.common.enums.RoleType;
import com.machpay.affiliate.common.exception.BadRequestException;
import com.machpay.affiliate.device.DeviceService;
import com.machpay.affiliate.entity.Device;
import com.machpay.affiliate.entity.Permission;
import com.machpay.affiliate.entity.Sender;
import com.machpay.affiliate.entity.SenderAddress;
import com.machpay.affiliate.entity.User;
import com.machpay.affiliate.redis.AuthToken;
import com.machpay.affiliate.redis.AuthTokenService;
import com.machpay.affiliate.security.TokenProvider;
import com.machpay.affiliate.security.UserPrincipal;
import com.machpay.affiliate.senderAddress.SenderAddressMapper;
import com.machpay.affiliate.senderAddress.SenderAddressRepository;
import com.machpay.affiliate.state.StateService;
import com.machpay.affiliate.user.UserService;
import com.machpay.affiliate.user.auth.dto.AccessTokenRequest;
import com.machpay.affiliate.user.auth.dto.AccessTokenResponse;
import com.machpay.affiliate.user.auth.dto.AuthFailureResponse;
import com.machpay.affiliate.user.auth.dto.AuthResponse;
import com.machpay.affiliate.user.auth.dto.LoginRequest;
import com.machpay.affiliate.user.auth.dto.Oauth2SignupRequest;
import com.machpay.affiliate.user.auth.dto.SignUpRequest;
import com.machpay.affiliate.user.auth.dto.SignUpRequestV2;
import com.machpay.affiliate.user.password.dto.CredentialVerificationRequest;
import com.machpay.affiliate.user.password.dto.CredentialVerificationResponse;
import com.machpay.affiliate.user.permission.PermissionRepository;
import com.machpay.affiliate.user.permission.PermissionService;
import com.machpay.affiliate.user.sender.SenderService;
import com.machpay.affiliate.user.sender.dto.SenderAuthResponse;
import com.machpay.affiliate.user.verification.ContactVerificationRepository;
import com.machpay.affiliate.user.verification.TwoFaVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private SenderService senderService;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private StateService stateService;

    @Autowired
    private TwoFaVerificationService twoFaVerificationService;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private UserService userService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private Messages messages;

    @Autowired
    private ContactVerificationRepository contactVerificationRepository;

    @Autowired
    private SenderAddressMapper senderAddressMapper;

    @Autowired
    private SenderAddressRepository senderAddressRepository;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PermissionRepository permissionRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userService.findByEmail(loginRequest.getEmail());
        Device device = deviceService.getDeviceByUserAndFingerprint(user, loginRequest.getDevice());
        String token = authenticate(loginRequest.getEmail(), loginRequest.getPassword(), device);

        return buildAuthResponse(user, token);
    }

    @Transactional
    public AuthResponse signUpV2(SignUpRequestV2 signUpRequestV2, HttpServletRequest request) {
        if (!stateService.isStateAvailable(signUpRequestV2.getState())) {
            throw new BadRequestException("The state provided is not available.");
        }

        if (!contactVerificationRepository.existsByContactAndLockedIsFalseAndVerifiedIsTrue(signUpRequestV2.getPhoneNumber())) {
            throw new BadRequestException("The phone number provided is not verified.");
        }

        if (!contactVerificationRepository.existsByContactAndLockedIsFalseAndVerifiedIsTrue(signUpRequestV2.getEmail())) {
            throw new BadRequestException("The email provided is not verified.");
        }

        if (senderService.isEmailDuplicate(signUpRequestV2.getEmail())) {
            throw new BadRequestException("Sender with given email already exists.");
        }

        if (senderService.isPhoneDuplicate(signUpRequestV2.getCountryCode().concat(signUpRequestV2.getPhoneNumber()).trim())) {
            throw new BadRequestException("Sender with given phone number already exists.");
        }

        Sender sender = senderService.createV2(signUpRequestV2, request);
        sender.setPhoneNumberVerified(true);

        User user = userService.findByEmail(signUpRequestV2.getEmail());
        user.setEmailVerified(true);
        userService.save(user);

        SenderAddress senderAddress = senderAddressMapper.toSenderAddress(signUpRequestV2, sender);
        senderAddressRepository.save(senderAddress);

        Set<Permission> permissions= permissionService.createDefaultPermissions(user);
        permissionRepository.saveAll(permissions);

        deviceService.create(sender, signUpRequestV2.getDevice(), request);
        Device device = deviceService.getDeviceByUserAndFingerprint(sender, signUpRequestV2.getDevice());
        String token = authenticate(signUpRequestV2.getEmail(), signUpRequestV2.getPassword(), device);

        return buildAuthResponse(sender, token);
    }

    private String authenticate(String email, String password, Device device) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        return tokenProvider.createAccessToken(userPrincipal.getId(), device);
    }

    public AuthResponse buildAuthResponse(User user, String token) {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(token);
        authResponse.setRoles(user.getRoles().stream()
                .map(role -> RoleType.valueOf(role.getName().toString()).toString().split("_")[1])
                .collect(Collectors.toList()));

        return authResponse;
    }

    public AuthFailureResponse buildAuthFailureResponse(User user, String token, Device device) {
        AuthFailureResponse authFailureResponse = new AuthFailureResponse();
        authFailureResponse.setToken(token);
        authFailureResponse.setRoles(user.getRoles().stream()
                .map(role -> RoleType.valueOf(role.getName().toString()).toString().split("_")[1])
                .collect(Collectors.toList()));

        authFailureResponse.setError(DeviceVerification.VERIFICATION_REQUIRED.getValue());
        authFailureResponse.setDevice_id(device.getId().toString());

        return authFailureResponse;
    }

    public SenderAuthResponse getGuestInfo(Long senderId) {
        Sender sender = senderService.findById(senderId);

        return senderService.buildSenderAuthResponse(sender);
    }

    private void grantNewAuthentication(Sender sender) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> updatedAuthorities = new ArrayList<>(authentication.getAuthorities());
        List<String> privileges = UserPrincipal.getPrivileges(sender.getRoles());

        for (String privilege : privileges) {
            updatedAuthorities.add(new SimpleGrantedAuthority(privilege));
        }

        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
                authentication.getCredentials(),
                updatedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }

    @Transactional
    public AccessTokenResponse refreshAccessToken(AccessTokenRequest accessTokenRequest) {
        AuthToken authToken = authTokenService.getAuthToken(accessTokenRequest.getReferenceToken());
        User user = userService.findById(authToken.getUserId());
        Device device = deviceService.getDeviceByUserAndFingerprint(user, accessTokenRequest.getDevice());
        String referenceToken = tokenProvider.createAccessToken(authToken.getUserId(), device);

        logger.info("Removing expired pair of auth tokens from redis with referenceId:{}",
                accessTokenRequest.getReferenceToken());
        authTokenService.deleteAuthTokenByReferenceToken(accessTokenRequest.getReferenceToken());

        return new AccessTokenResponse(referenceToken);
    }

    public CredentialVerificationResponse isCredentialsValid(long userId, CredentialVerificationRequest credentialVerificationRequest) {
        User user = userService.findById(userId);

        if (BCrypt.checkpw(credentialVerificationRequest.getPassword(), user.getPassword())) {
            return new CredentialVerificationResponse(CredentialVerificationStatus.MATCHED);
        }

        return new CredentialVerificationResponse(CredentialVerificationStatus.NOT_MATCHED);
    }
}
