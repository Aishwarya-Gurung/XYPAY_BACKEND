package com.machpay.affiliate.device;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.machpay.affiliate.common.Constants;
import com.machpay.affiliate.common.Messages;
import com.machpay.affiliate.common.enums.ClientType;
import com.machpay.affiliate.common.enums.LockReason;
import com.machpay.affiliate.common.enums.AdminCredentials;
import com.machpay.affiliate.common.enums.DeviceVerification;
import com.machpay.affiliate.common.exception.BadRequestException;
import com.machpay.affiliate.common.exception.TokenExpiredException;
import com.machpay.affiliate.common.exception.UnauthorizedAccessException;
import com.machpay.affiliate.config.TestUserConfig;
import com.machpay.affiliate.device.dto.DeviceResponse;
import com.machpay.affiliate.device.dto.DeviceVerificationRequest;
import com.machpay.affiliate.entity.Device;
import com.machpay.affiliate.entity.User;
import com.machpay.affiliate.redis.AuthToken;
import com.machpay.affiliate.redis.AuthTokenService;
import com.machpay.affiliate.security.TokenProvider;
import com.machpay.affiliate.security.UserPrincipal;
import com.machpay.affiliate.twilio.VerificationCodeGenerator;
import com.machpay.affiliate.user.UserService;
import com.machpay.affiliate.user.auth.AuthService;
import com.machpay.affiliate.user.auth.dto.AuthFailureResponse;
import com.machpay.affiliate.user.auth.dto.AuthResponse;
import com.machpay.affiliate.user.auth.dto.LoginRequest;
import com.machpay.affiliate.user.auth.dto.SignUpRequest;
import com.machpay.affiliate.util.HttpServletRequestUtils;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DeviceService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);

    public static final UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private Messages messages;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private TestUserConfig testUserConfig;

    @Transactional
    public void saveDevice(Device device) {
        deviceRepository.save(device);
    }

    public Device getDeviceByUserAndId(User user, UUID id) {
        return deviceRepository.findByUserAndId(user, id)
                .orElseThrow(() -> new BadRequestException("Device not found."));
    }

    public Device getDeviceByUserAndFingerprint(User user, String fingerprint) {
        return deviceRepository.findByUserAndFingerprint(user, fingerprint);
    }

    public List<Device> getAllByUser(User user) {
        return deviceRepository.findAllByUser(user);
    }

    public List<DeviceResponse> getDeviceList(User user, String referenceToken) {
        List<Device> devices = deviceRepository.findAllByUserAndDeviceActiveIsTrueOrderByLastLoginDateDesc(user);
        List<DeviceResponse> deviceResponses = deviceMapper.toDeviceResponse(devices);
        AuthToken authToken = authTokenService.getAuthToken(referenceToken);

        deviceResponses.forEach(deviceResponse -> {
            if (deviceResponse.getFingerprint().equals(authToken.getDeviceFingerprint())) {
                deviceResponse.setCurrentDevice(true);
            }
        });

        return deviceResponses;
    }

    public void create(User user, String fingerprint, HttpServletRequest request) {
        Device device = setDeviceInfo(user, fingerprint, request);
        device.setDeviceActive(true);
        device.setDeviceVerified(true);
        device.setVerificationRequired(false);
        device.setVerificationCodeResendAttempts(0);

        saveDevice(device);
    }

    @Transactional
    public Device addNewDevice(User user, LoginRequest loginRequest, HttpServletRequest request) {
        Device device;

        if (isTestUser(user.getEmail())) {
            device = setDeviceInfo(user, loginRequest.getDevice(), request);
            device.setDeviceActive(true);
            device.setDeviceVerified(true);
            device.setVerificationRequired(false);
            saveDevice(device);
        } else {
            String verificationCode = user.getEmail().equalsIgnoreCase(AdminCredentials.EMAIL.getValue()) ?
                    Constants.DEFAULT_DEVICE_VERIFICATION_CODE : VerificationCodeGenerator.generate();
            device = setDeviceInfo(user, loginRequest.getDevice(), request);
            device.setDeviceActive(false);
            device.setDeviceVerified(false);
            device.setVerificationRequired(true);
            device.setVerificationCode(verificationCode);
            device.setExpiryDate(LocalDateTime.now().plusMinutes(30L));
            saveDevice(device);
        }

        return getDeviceByUserAndFingerprint(user, loginRequest.getDevice());
    }

    private Device setDeviceInfo(User user, String fingerprint, HttpServletRequest request) {
        Device device = new Device();

        if (HttpServletRequestUtils.isRequestFromNativeApp(request)) {
            String[] deviceHeader = HttpServletRequestUtils.parseDeviceHeader(request.getHeader("device"));

            device.setUser(user);
            device.setOs(deviceHeader[0].trim());
            device.setBrowserVersion(deviceHeader[1].trim());
            device.setDeviceType(deviceHeader[2].trim());
            device.setIp(HttpServletRequestUtils.getIPAddress(request));
            device.setClientType(ClientType.MOBILE_APP);
            device.setFingerprint(fingerprint);

            return device;
        }

        if (HttpServletRequestUtils.isRequestFromWebApp(request)) {
            ReadableUserAgent agent = parser.parse(request.getHeader("User-Agent"));

            device.setUser(user);
            device.setBrowser(agent.getName());
            device.setLastLoginDate(LocalDateTime.now());
            device.setFingerprint(fingerprint);
            device.setOs(agent.getOperatingSystem().getName());
            device.setDeviceType(agent.getDeviceCategory().getName());
            device.setIp(HttpServletRequestUtils.getIPAddress(request));
            device.setBrowserVersion(agent.getVersionNumber().toVersionString());
            device.setClientType(ClientType.WEB_APP);

            return device;
        }

        device.setUser(user);
        device.setIp(HttpServletRequestUtils.getIPAddress(request));
        device.setClientType(ClientType.WEB_APP);

        return device;
    }


    public void verifyDevice(User user,
                             DeviceVerificationRequest deviceVerificationRequest,
                             String deviceId,
                             String referenceToken) {
        Device device = getDeviceByUserAndId(user, UUID.fromString(deviceId));

        // Note: Increasing device.getVerificationAttempt() by 1 because it is new attempt and hasn't been stored in the database
        int newDeviceVerificationAttempt = device.getVerificationAttempt() + 1;
        logger.info("Device verification request is received for user({}) with OTP code {}. Verification attempt is {}",
                user.getEmail(), deviceVerificationRequest.getDevice(), newDeviceVerificationAttempt);

        verifyOTP(device, deviceVerificationRequest.getDevice());
        authTokenService.updateDeviceVerificationStatus(referenceToken);
    }

    public void checkVerificationAttempts(Device device) {
        int verificationAttemptsLeft = getRemainingVerificationAttemptsCount(device);

        if (verificationAttemptsLeft > 0) {
            String errorMessage = messages.get("user.account.invalidCode", String.valueOf(verificationAttemptsLeft));

            throw new BadRequestException(errorMessage);
        } else {
            User user = device.getUser();

            lockAndInvalidateToken(user);

            String errorMessage = messages.get("user.account.deviceVerificationLimitExceeded");

            throw new BadRequestException(errorMessage);
        }
    }

    private void lockAndInvalidateToken(User user) {
        userService.lock(user.getEmail(), LockReason.DEVICE_VERIFICATION_ATTEMPT_LIMIT_EXCEEDED);

        authTokenService.deleteAuthTokenByUserId(user.getId());
    }

    private void verifyOTP(Device device, String verificationCode) {

        if (device.getExpiryDate().isAfter(LocalDateTime.now()) && device.getVerificationCode().equals(verificationCode)) {
            device.setDeviceVerified(true);
            device.setDeviceActive(true);
            device.setVerificationRequired(false);
            device.setExpiryDate(LocalDateTime.now());
            device.setLastLoginDate(LocalDateTime.now());
            saveDevice(device);
        } else {
            device.setVerificationAttempt(device.getVerificationAttempt() + 1);
            deviceRepository.save(device);

            checkVerificationAttempts(device);
        }
    }

    private int getRemainingVerificationAttemptsCount(Device device) {
        return Constants.MAX_OTP_VERIFICATION_ATTEMPT - device.getVerificationAttempt();
    }

    public void resendVerificationCode(User user, String deviceId) {
        Device device = getDeviceByUserAndId(user, UUID.fromString(deviceId));

        if (device.isDeviceVerified()) {
            logger.info("User({}) device with deviceId: {} is already verified.", user.getEmail(), deviceId);
            throw new BadRequestException("Your device is already verified.");
        }

        if (!device.isDeviceActive() && !device.isDeviceVerified() &&
                (device.getVerificationCodeResendAttempts() >= Constants.RESEND_DEVICE_VERIFICATION_CODE_LIMIT)) {
            userService.lock(user.getEmail(),
                    LockReason.DEVICE_VERIFICATION_CODE_RESEND_LIMIT_EXCEEDED);
            authTokenService.deleteAuthTokenByUserId(user.getId());
            logger.info("Sender {} is locked because {}", user.getEmail(),
                    Constants.RESEND_DEVICE_VERIFICATION_CODE_LIMIT_EXCEEDED);

            String message = messages.get("user.account.deviceVerificationResendLimitExceeded");

            throw new BadRequestException(message);
        }

        String verificationCode = user.getEmail().equalsIgnoreCase(AdminCredentials.EMAIL.getValue()) ?
                Constants.DEFAULT_DEVICE_VERIFICATION_CODE : VerificationCodeGenerator.generate();

        device.setVerificationCode(verificationCode);
        device.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        device.setVerificationAttempt(0);
        device.setVerificationCodeResendAttempts(device.getVerificationCodeResendAttempts() + 1);
        saveDevice(device);
    }

    @Transactional
    public void enableDevice(User user, String fingerprint, HttpServletRequest request) {
        String verificationCode = user.getEmail().equalsIgnoreCase(AdminCredentials.EMAIL.getValue()) ?
                Constants.DEFAULT_DEVICE_VERIFICATION_CODE : VerificationCodeGenerator.generate();

        Device device = getDeviceByUserAndFingerprint(user, fingerprint);
        device.setIp(HttpServletRequestUtils.getIPAddress(request));
        device.setVerificationCode(verificationCode);
        device.setExpiryDate(LocalDateTime.now().plusMinutes(30L));
        device.setVerificationRequired(true);
        device.setDeviceVerified(false);
        device.setDeviceActive(false);
        device.setVerificationCodeResendAttempts(0);
        saveDevice(device);
    }

    //TODO: Refactor this method
    @Transactional
    public void checkDevice(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userService.findById(userPrincipal.getId());
        LoginRequest loginRequest = HttpServletRequestUtils.getLoginRequest(request);
        Device device = getDeviceByUserAndFingerprint(user, loginRequest.getDevice());

        if (device == null) {
            logger.info("User {} is successfully logged-in from new device(Verification Pending) with fingerprint {}",
                    loginRequest.getEmail(),
                    loginRequest.getDevice());
            device = addNewDevice(user, loginRequest, request);

            if (isTestUser(user.getEmail())) {
                sendAuthResponse(user, device, response);

                return;
            }

            sendAuthFailureResponse(user, device, response);
        }

        if (!device.isDeviceActive() && !device.isVerificationRequired()) {
            logger.info("User {} is successfully logged-in form new device(Verification Pending) {} which was removed" +
                    " previously by user.", loginRequest.getEmail(), device.getFingerprint());

            if (isTestUser(user.getEmail())) {
                device.setDeviceActive(true);
                device.setDeviceVerified(true);
                device.setVerificationRequired(false);
                saveDevice(device);
                sendAuthResponse(user, device, response);

                return;
            }

            enableDevice(user, loginRequest.getDevice(), request);
            sendAuthFailureResponse(user, device, response);
        }

        if (!device.isDeviceActive() && device.isVerificationRequired()) {
            logger.info("User {} successfully logged-in but verification of device {} is pending",
                    user.getEmail(), device.getFingerprint());

            if (isTestUser(user.getEmail())) {
                device.setDeviceActive(true);
                device.setDeviceVerified(true);
                device.setVerificationRequired(false);
                saveDevice(device);
                sendAuthResponse(user, device, response);

                return;
            }

            updateLastLoginDate(device);
            sendAuthFailureResponse(user, device, response);
        }

        logger.info("User {} is successfully logged-in from device(Verified) with fingerprint {}",
                loginRequest.getEmail(), loginRequest.getDevice());
        updateLastLoginDate(device);
        sendAuthResponse(user, device, response);
    }

    private void updateLastLoginDate(Device device) {
        device.setLastLoginDate(LocalDateTime.now());
        saveDevice(device);
    }

    private void sendAuthResponse(User user, Device device,
                                  HttpServletResponse response) throws IOException {
        String token = tokenProvider.createAccessToken(user.getId(), device);
        AuthResponse authResponse = authService.buildAuthResponse(user, token);
        response.setStatus(HttpStatus.OK.value());
        objectMapper.writeValue(response.getWriter(), authResponse);
    }

    public void sendAuthFailureResponse(User user, Device device, HttpServletResponse response) throws IOException {
        String token = tokenProvider.createAccessToken(user.getId(), device);
        AuthFailureResponse authFailureResponse = authService.buildAuthFailureResponse(user, token, device);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        objectMapper.writeValue(response.getWriter(), authFailureResponse);

    }

    @Transactional
    public void removeDevice(User user, String deviceId, HttpServletRequest request) {
        Device device = getDeviceByUserAndId(user, UUID.fromString(deviceId));
        AuthToken authToken = authTokenService.getAuthToken(HttpServletRequestUtils.getReferenceToken(request));

        if (authToken.getDeviceFingerprint().equals(device.getFingerprint())) {
            throw new BadRequestException("You can not remove the currently logged in device.");
        }

        disableDevice(device);
        authTokenService.deleteAuthTokenByDeviceFingerprint(device.getFingerprint());
    }

    private void disableDevice(Device device) {
        device.setDeviceActive(false);
        device.setDeviceVerified(false);
        device.setVerificationRequired(false);
        saveDevice(device);
    }

    public void unlockDevice(User user, LockReason lockReason) {
        List<Device> devices = getUnverifiedDevices(user);
        devices.forEach(this::resetResendAttempt);
    }

    public void checkDeviceStatus(HttpServletRequest request) {
        String referenceToken = HttpServletRequestUtils.getReferenceToken(request);
        AuthToken authToken = authTokenService.getAuthToken(referenceToken);

        if (!authToken.isDeviceVerified()) {
            logger.info("User {} tried to access the system with unregistered device with fingerprint {}",
                    authToken.getUserId(), authToken.getDeviceFingerprint());
            throw new UnauthorizedAccessException(DeviceVerification.VERIFICATION_REQUIRED.getValue());
        }
    }

    public List<Device> getUnverifiedDevices(User user) {
        return deviceRepository.findAllByUserAndVerificationRequiredIsTrueAndDeviceActiveIsFalseAndVerificationAttemptIsGreaterThanOrVerificationCodeResendAttemptsIsGreaterThan(
                user, Constants.MAX_OTP_VERIFICATION_ATTEMPT - 1, Constants.RESEND_DEVICE_VERIFICATION_CODE_LIMIT - 1);
    }

    public Device getDevice(User user, HttpServletRequest request) {
        String referenceToken = HttpServletRequestUtils.getReferenceToken(request);
        String deviceFingerprint = authTokenService.getAuthToken(referenceToken).getDeviceFingerprint();

        return getDeviceByUserAndFingerprint(user, deviceFingerprint);
    }

    @Transactional
    public void resetResendAttempt(Device device) {
        device.setVerificationCodeResendAttempts(0);
        device.setVerificationAttempt(0);
        saveDevice(device);
    }

    private boolean isTestUser(String email) {
        return testUserConfig.getUser().getEmail().equals(email.trim());
    }
}