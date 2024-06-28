package com.machpay.affiliate.user.verification;

import com.machpay.affiliate.common.Constants;
import com.machpay.affiliate.common.Messages;
import org.springframework.util.StringUtils;
import com.machpay.affiliate.common.enums.DeviceType;
import com.machpay.affiliate.common.enums.LockReason;
import com.machpay.affiliate.common.enums.ServerSentEvent;
import com.machpay.affiliate.common.enums.ApplicationEnvironment;
import com.machpay.affiliate.common.enums.DeviceVerificationStatus;
import com.machpay.affiliate.common.exception.BadRequestException;
import com.machpay.affiliate.common.exception.TokenExpiredException;
import com.machpay.affiliate.entity.Sender;
import com.machpay.affiliate.entity.TwoFaVerification;
import com.machpay.affiliate.entity.User;
import com.machpay.affiliate.redis.AuthTokenService;
import com.machpay.affiliate.serversentevent.ServerSentEventService;
import com.machpay.affiliate.twilio.TwilioSMSService;
import com.machpay.affiliate.twilio.VerificationCodeGenerator;
import com.machpay.affiliate.user.UserService;
import com.machpay.affiliate.user.sender.SenderRepository;
import com.machpay.affiliate.user.sender.SenderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Service;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.time.LocalDateTime;

@Service
public class TwoFaVerificationService {

    private static final Logger logger = LoggerFactory.getLogger(TwoFaVerificationService.class);

    @Autowired
    private Environment environment;

    @Autowired
    private Messages messages;

    @Autowired
    private TwoFaVerificationRepository twoFaVerificationRepository;

    @Autowired
    private SenderService senderService;

    @Autowired
    private SenderRepository senderRepository;

    @Autowired
    private TwilioSMSService twilioSmsService;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private UserService userService;

    @Autowired
    private ServerSentEventService serverSentEventService;

    public void createDeviceVerification(Sender sender, DeviceType deviceType) {

        if (!isDeviceVerified(sender, deviceType))
            checkVerificationToken(sender, deviceType);

        else
            throw new BadRequestException("Your " + deviceType.name() + " is already verified.");
    }

    private boolean isDeviceVerified(Sender sender, DeviceType deviceType) {
        return DeviceType.PHONE.equals(deviceType)
                ? sender.isPhoneNumberVerified()
                : sender.isEmailVerified();

    }

    private void checkVerificationToken(Sender sender, DeviceType deviceType) {
        TwoFaVerification twoFaVerification = Boolean.TRUE.equals(twoFaVerificationRepository.existsByUserAndType(sender, deviceType))
                ? checkTwoFAVerificationResendAttempts(sender, deviceType)
                : createVerificationToken(sender, deviceType);
    }

    private TwoFaVerification checkTwoFAVerificationResendAttempts(Sender sender, DeviceType deviceType) {
        TwoFaVerification twoFaVerification = getTwoFaVerification(sender.getId(),
                deviceType);

        if (twoFaVerification.getResendAttempt() >= Constants.RESEND_VERIFICATION_CODE_LIMIT) {
            LockReason lockReason = DeviceType.EMAIL.equals(deviceType) ? LockReason.EMAIL_VERIFICATION_CODE_RESEND_LIMIT_EXCEEDED :
                    LockReason.PHONE_VERIFICATION_CODE_RESEND_LIMIT_EXCEEDED;

            lockAndInvalidateToken(twoFaVerification.getUser(), lockReason);
            logger.info("{} verification code resend limit exceeded for user {}.", deviceType, sender.getEmail());

            String errorMessage = messages.get("user.account.resendVerificationCodeLimitExceeded", deviceType.getType());

            throw new BadRequestException(errorMessage);
        } else {
            return updateVerificationToken(twoFaVerification, deviceType);
        }
    }

    @Transactional
    public TwoFaVerification createVerificationToken(Sender sender, DeviceType deviceType) {
        TwoFaVerification twoFaVerification = new TwoFaVerification();

        twoFaVerification.setUser(sender);
        twoFaVerification.setType(deviceType);
        twoFaVerification.setToken(generateVerificationCode(deviceType));
        twoFaVerification.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        twoFaVerification.setStatus(DeviceVerificationStatus.PENDING);
        twoFaVerification.setVerificationAttempt(0);

        return twoFaVerificationRepository.save(twoFaVerification);
    }

    @Transactional
    public TwoFaVerification updateVerificationToken(TwoFaVerification twoFaVerification, DeviceType deviceType) {
        twoFaVerification.setToken(generateVerificationCode(deviceType));
        twoFaVerification.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        twoFaVerification.setVerificationAttempt(0);
        twoFaVerification.setResendAttempt(twoFaVerification.getResendAttempt() + 1);

        return twoFaVerificationRepository.save(twoFaVerification);
    }

    private void lockAndInvalidateToken(User user, LockReason lockReason) {
        userService.lock(user.getEmail(), lockReason);
        authTokenService.deleteAuthTokenByUserId(user.getId());
    }

    @Transactional
    public void resetResendAttempt(Long userId, DeviceType deviceType) {
        TwoFaVerification twoFaVerification = getTwoFaVerification(userId, deviceType);
        twoFaVerification.setResendAttempt(0);
        twoFaVerification.setVerificationAttempt(0);
        twoFaVerificationRepository.save(twoFaVerification);
    }

    private String generateVerificationCode(DeviceType deviceType) {
        if (DeviceType.PHONE.equals(deviceType)) {
            if (environment.acceptsProfiles(Profiles.of(ApplicationEnvironment.PROD.getEnvironment()))) {
                return VerificationCodeGenerator.generate();
            }

            return Constants.DEFAULT_PHONE_VERIFICATION_CODE;
        }


        return VerificationCodeGenerator.generate();
    }


    private void sendPhoneVerificationCode(Sender sender, String token) {
        if (environment.acceptsProfiles(Profiles.of(ApplicationEnvironment.PROD.getEnvironment()))) {
            String phoneNumber = sender.getPhoneNumber();
            String message = token.concat(Constants.PHONE_NUMBER_VERIFICATION_SMS);

            if (sender.isNewUser()) {
                twilioSmsService.sendVerificationCodeAsynchronously(phoneNumber, message);
            } else {
                twilioSmsService.sendVerificationCodeSynchronously(phoneNumber, message);
            }
        }
    }

    public void verifyToken(long userId, String token, DeviceType deviceType) {
        TwoFaVerification twoFaVerification = getTwoFaVerification(userId, deviceType);
        Sender sender = senderService.findById(userId);

        // Note: Increasing twoFaVerification.getVerificationAttempt() by 1 because it is new attempt and hasn't been stored in the database
        int newVerificationAttempt = twoFaVerification.getVerificationAttempt() + 1;
        logger.info("{} verification request received from sender({}) with OTP code {}. Attempt count is {}",
                deviceType.name(), sender.getEmail(), token, newVerificationAttempt);

        incrementVerificationAttempt(twoFaVerification);

        if (token.isEmpty()) {
            throw new BadRequestException("Verification token is empty.");
        }

        if (twoFaVerification.getExpiryDate().isBefore(LocalDateTime.now())) {
            logger.info("Verification token: [{}] expired with expiry date [{}] before [{}]", token,
                    twoFaVerification.getExpiryDate(), LocalDateTime.now());
            throw new TokenExpiredException("Verification token is expired.");
        }

        switch (deviceType) {
            case PHONE:
                verifyPhone(sender, twoFaVerification, token);

                if (!sender.isEmailVerified()) {
                    createDeviceVerification(sender, DeviceType.EMAIL);
                }

                serverSentEventService.emitEvent(ServerSentEvent.PHONE_NUMBER_VERIFIED, sender.getReferenceId());
                break;

            case EMAIL:
                verifyEmail(twoFaVerification, token);
                UUID referenceId = senderService.findById(twoFaVerification.getUser().getId()).getReferenceId();
                serverSentEventService.emitEvent(ServerSentEvent.EMAIL_VERIFIED, referenceId);
                break;
        }
    }

    private int getRemainingVerificationAttemptsCount(TwoFaVerification twoFaVerification) {
        return Constants.MAX_OTP_VERIFICATION_ATTEMPT - twoFaVerification.getVerificationAttempt();
    }

    public TwoFaVerification getTwoFaVerification(long id, DeviceType deviceType) {
        return twoFaVerificationRepository.findByUserIdAndType(id, deviceType)
                .orElseThrow(() -> new BadRequestException("User has no verification data"));
    }

    @Transactional
    public void verifyEmail(TwoFaVerification twoFaVerification, String token) {
        if (twoFaVerification.getToken().equals(token)) {
            twoFaVerification.setVerifiedAt(LocalDateTime.now());
            twoFaVerification.setStatus(DeviceVerificationStatus.VERIFIED);
            twoFaVerification.getUser().setEmailVerified(true);
            twoFaVerification.setExpiryDate(LocalDateTime.now());
            twoFaVerificationRepository.save(twoFaVerification);
        } else {
            checkVerificationAttempts(twoFaVerification);
        }
    }

    @Transactional
    public void verifyPhone(Sender sender, TwoFaVerification twoFaVerification, String token) {
        if (twoFaVerification.getToken().equals(token)) {
            twoFaVerification.setVerifiedAt(LocalDateTime.now());
            twoFaVerification.setStatus(DeviceVerificationStatus.VERIFIED);
            twoFaVerificationRepository.save(twoFaVerification);

            sender.setPhoneNumberVerified(true);
            senderRepository.save(sender);
        } else {
            checkVerificationAttempts(twoFaVerification);
        }
    }

    private void incrementVerificationAttempt(TwoFaVerification twoFaVerification) {
        twoFaVerification.setVerificationAttempt(twoFaVerification.getVerificationAttempt() + 1);
        twoFaVerificationRepository.save(twoFaVerification);
    }

    @Transactional
    public void updateVerificationStatus(Long senderId, DeviceType deviceType) {
        TwoFaVerification twoFaVerification = getTwoFaVerification(senderId, deviceType);
        twoFaVerification.setResendAttempt(0);
        twoFaVerification.setVerificationAttempt(0);
        twoFaVerification.setStatus(DeviceVerificationStatus.PENDING);

        twoFaVerificationRepository.save(twoFaVerification);
    }

    public boolean isEmailVerificationInProgress(long userId) {
        return twoFaVerificationRepository.findByUserIdAndType(userId, DeviceType.EMAIL).isPresent();
    }

    public void checkVerificationAttempts(TwoFaVerification twoFaVerification) {
        int attemptsLeft = getRemainingVerificationAttemptsCount(twoFaVerification);

        if (attemptsLeft > 0) {
            String errorMessage = messages.get("user.account.invalidCode", String.valueOf(attemptsLeft));

            throw new BadRequestException(errorMessage);
        } else {
            User user = twoFaVerification.getUser();
            DeviceType deviceType = twoFaVerification.getType();

            LockReason lockReason = DeviceType.EMAIL.equals(deviceType) ? LockReason.EMAIL_VERIFICATION_ATTEMPT_LIMIT_EXCEEDED :
                    LockReason.PHONE_VERIFICATION_ATTEMPT_LIMIT_EXCEEDED;
            lockAndInvalidateToken(user, lockReason);

            String contactType = StringUtils.capitalize(deviceType.getType());
            String errorMessage = messages.get("user.account.verificationLimitExceeded", contactType);
            throw new BadRequestException(errorMessage);
        }
    }
}
