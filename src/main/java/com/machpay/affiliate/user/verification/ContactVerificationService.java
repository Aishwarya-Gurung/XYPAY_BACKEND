package com.machpay.affiliate.user.verification;

import com.machpay.affiliate.common.Constants;
import com.machpay.affiliate.common.Messages;
import com.machpay.affiliate.common.enums.LockReason;
import com.machpay.affiliate.common.enums.ApplicationEnvironment;
import com.machpay.affiliate.common.enums.DeviceType;
import com.machpay.affiliate.common.exception.BadRequestException;
import com.machpay.affiliate.common.exception.TokenExpiredException;
import com.machpay.affiliate.entity.ContactVerification;
import com.machpay.affiliate.entity.Sender;
import com.machpay.affiliate.twilio.TwilioSMSService;
import com.machpay.affiliate.user.sender.SenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class ContactVerificationService {

    private static final Logger logger = LoggerFactory.getLogger(ContactVerificationService.class);

    @Autowired
    private Messages messages;

    @Autowired
    private Environment environment;

    @Autowired
    private SenderService senderService;

    @Autowired
    private TwilioSMSService twilioSmsService;

    @Autowired
    private ContactVerificationRepository contactVerificationRepository;

    public void sendVerificationCode(DeviceType deviceType, String contact) {
        boolean isExistingPhone = DeviceType.PHONE.equals(deviceType) && senderService.isPhoneNumberDuplicateAndUserDeleted("+1".concat(contact));
        boolean isExistingEmail = DeviceType.EMAIL.equals(deviceType) && senderService.isEmailDuplicateAndUserDeleted(contact);

        if (isExistingPhone) {
            String message = messages.get("user.account.phoneNumberAlreadyDeleted");

            throw new BadRequestException(message);
        }

        if (isExistingEmail) {
            String message = messages.get("user.account.emailAlreadyDeleted");

            throw new BadRequestException(message);
        }

        if (DeviceType.PHONE.equals(deviceType) && senderService.isPhoneDuplicateAndVerified("+1".concat(contact))) {
            String message = messages.get("user.account.phoneNumberAlreadyExist");

            throw new BadRequestException(message);
        }

        if (DeviceType.EMAIL.equals(deviceType) && senderService.isEmailDuplicateAndVerified(contact)) {
            String message = messages.get("user.account.emailAlreadyExist");

            throw new BadRequestException(message);
        }

        ContactVerification contactVerification = contactVerificationRepository.existsByTypeAndContact(deviceType, contact)
                ? checkVerificationResendAttempts(deviceType, contact)
                : createVerificationToken(deviceType, contact);

    }

    @Transactional
    public ContactVerification createVerificationToken(DeviceType deviceType, String contact) {
        ContactVerification contactVerification = new ContactVerification();

        contactVerification.setContact(contact);
        contactVerification.setType(deviceType);
        contactVerification.setToken(generateVerificationCode(deviceType));
        contactVerification.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        contactVerification.setVerificationAttempt(0);

        return contactVerificationRepository.save(contactVerification);
    }

    private String generateVerificationCode(DeviceType deviceType) {
        return Constants.DEFAULT_PHONE_VERIFICATION_CODE;
    }

    private ContactVerification checkVerificationResendAttempts(DeviceType deviceType, String contact) {
        ContactVerification contactVerification = getByDeviceTypeAndContact(deviceType, contact);

        boolean isContactLockedAndLocalDateBeforeResetTime = contactVerification.isLocked() &&
                LocalDateTime.now().isBefore(contactVerification.getLockedAt().plusMinutes(30));

        boolean isVerificationAttemptExceeded = contactVerification.getVerificationAttempt() >= Constants.MAX_OTP_VERIFICATION_ATTEMPT;

        if (isContactLockedAndLocalDateBeforeResetTime && isVerificationAttemptExceeded) {
            String message = messages.get("user.otp.attemptLimitExceed", StringUtils.capitalize(deviceType.getType()));

            throw new BadRequestException(message);
        }

        if (!isContactLockedAndLocalDateBeforeResetTime && isVerificationAttemptExceeded) {
            unlock(contact, contactVerification);
        }

        int newResendAttempt = contactVerification.getResendAttempt() + 1;
        boolean isResendAttemptExceeded = newResendAttempt > Constants.RESEND_VERIFICATION_CODE_LIMIT;

        if (isResendAttemptExceeded) {
            //todo: automate time from application.yml
            if (contactVerification.isLocked() && LocalDateTime.now().isAfter(contactVerification.getLockedAt().plusMinutes(30))) {
                contactVerification.setLocked(false);
                contactVerification.setLockedAt(null);
                contactVerification.setLockReason(null);
                contactVerification.setResendAttempt(1);
                contactVerification.setVerificationAttempt(0);
                contactVerification.setToken(generateVerificationCode(deviceType));
                contactVerification.setExpiryDate(LocalDateTime.now().plusMinutes(30));

                return contactVerificationRepository.save(contactVerification);
            }

            LockReason lockReason = getLockReason(deviceType);

            lock(contact, contactVerification, lockReason);

            contactVerificationRepository.save(contactVerification);

            logger.info("{} verification code resend limit exceeded for contact {}.", deviceType, contact);

            String message = messages.get("user.otp.resendLimitExceed", StringUtils.capitalize(deviceType.getType()));

            throw new BadRequestException(message);
        } else {
            return updateVerificationToken(contactVerification, deviceType);
        }
    }


    public ContactVerification getByDeviceTypeAndContact(DeviceType deviceType, String contact) {
        return contactVerificationRepository.findByTypeAndContact(deviceType, contact)
                .orElseThrow(() -> new BadRequestException("User has no verification data"));
    }


    private void sendPhoneVerificationCode(String contact, String token) {
        if (environment.acceptsProfiles(Profiles.of(ApplicationEnvironment.PROD.getEnvironment()))) {
            String phoneNumber = "+1".concat(contact);
            String message = token.concat(Constants.PHONE_NUMBER_VERIFICATION_SMS);

            twilioSmsService.sendVerificationCodeAsynchronously(phoneNumber, message);
        }
    }

    @Transactional
    public ContactVerification updateVerificationToken(ContactVerification contactVerification, DeviceType deviceType) {
        contactVerification.setToken(generateVerificationCode(deviceType));
        contactVerification.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        contactVerification.setVerificationAttempt(0);
        contactVerification.setResendAttempt(contactVerification.getResendAttempt() + 1);

        return contactVerificationRepository.save(contactVerification);
    }


    public void verify(DeviceType deviceType, String contact, String token) {
        ContactVerification contactVerification = getByDeviceTypeAndContact(deviceType, contact);

        logger.info("{} verification request received from contact({}) with OTP code {}. Attempt count is {}",
                deviceType.name(), contact, token, contactVerification.getVerificationAttempt() + 1);

        validateToken(deviceType, contact, token, contactVerification);

        if (contactVerification.getVerificationAttempt() < 5) {
            incrementVerificationAttempt(contactVerification);
        }

        switch (deviceType) {
            case PHONE:
                verifyPhone(contactVerification, token, contact, deviceType);

                break;

            case EMAIL:
                verifyEmail(contactVerification, token, contact, deviceType);

                break;
        }
    }

    public void verifySenderContact(long userId, DeviceType deviceType, String contact, String token) {
        ContactVerification contactVerification = getByDeviceTypeAndContact(deviceType, contact);
        Sender sender = senderService.findById(userId);

        if (DeviceType.PHONE.equals(deviceType) && !sender.getPhoneNumber().equals(contact)) {
            throw new BadRequestException("Sender phone number doesn't match.");
        }

        if (DeviceType.EMAIL.equals(deviceType) && !sender.getEmail().equals(contact)) {
            throw new BadRequestException("Sender email doesn't match.");
        }

        logger.info("{} verification request received from contact({}) with OTP code {} for sender({}). Attempt count is {}",
                deviceType.name(), contact, token, sender.getReferenceId(), contactVerification.getVerificationAttempt() + 1);

        validateToken(deviceType, contact, token, contactVerification);

        if (contactVerification.getVerificationAttempt() < 5) {
            incrementVerificationAttempt(contactVerification);
        }

        switch (deviceType) {
            case PHONE:
                verifyPhone(contactVerification, token, contact, deviceType);

                sender.setPhoneNumberVerified(true);
                senderService.save(sender);

                break;

            case EMAIL:
                verifyEmail(contactVerification, token, contact, deviceType);

                sender.setEmailVerified(true);
                senderService.save(sender);

                break;
        }
    }

    private void validateToken(DeviceType deviceType, String contact, String token, ContactVerification contactVerification) {
        if (token.isEmpty()) {
            throw new BadRequestException("Verification token is empty.");
        }

        if (contactVerification.getExpiryDate().isBefore(LocalDateTime.now())) {
            logger.info("Verification token: [{}] expired with expiry date [{}] before [{}]", token,
                    contactVerification.getExpiryDate(), LocalDateTime.now());
            throw new TokenExpiredException("Verification token is expired.");
        }

        int verificationAttempt = contactVerification.getVerificationAttempt() + 1;
        boolean isVerificationAttemptExceeded = verificationAttempt > Constants.MAX_OTP_VERIFICATION_ATTEMPT;

        if (isVerificationAttemptExceeded) {
            //todo: automate time from application.yml
            if (contactVerification.isLocked() && LocalDateTime.now().isAfter(contactVerification.getLockedAt().plusMinutes(30))) {
                unlock(contact, contactVerification);

                return;
            }

            LockReason lockReason = getLockReason(deviceType);

            lock(contact, contactVerification, lockReason);

            if (contactVerification.getVerificationAttempt() < Constants.MAX_OTP_VERIFICATION_ATTEMPT) {
                incrementVerificationAttempt(contactVerification);
            }

            logger.info("{} OTP verification attempt limit exceeded for contact {}.", deviceType, contact);

            String message = messages.get("user.account.verificationLimitExceeded", StringUtils.capitalize(deviceType.getType()));

            throw new BadRequestException(message);
        }
    }

    @Transactional
    public void verifyEmail(ContactVerification contactVerification, String token, String contact, DeviceType deviceType) {
        if (contactVerification.getToken().equals(token)) {
            contactVerification.setVerifiedAt(LocalDateTime.now());
            contactVerification.setVerified(true);
            contactVerification.setLocked(false);
            contactVerification.setLockReason(null);
            contactVerification.setLockedAt(null);
            contactVerification.setResendAttempt(0);
            contactVerification.setVerificationAttempt(0);
            contactVerification.setExpiryDate(LocalDateTime.now());
            contactVerificationRepository.save(contactVerification);
        } else {
            checkVerificationAttemptsAndGenerateException(contactVerification, deviceType);
        }
    }

    @Transactional
    public void verifyPhone(ContactVerification contactVerification, String token, String contact, DeviceType deviceType) {
        if (contactVerification.getToken().equals(token)) {
            contactVerification.setVerifiedAt(LocalDateTime.now());
            contactVerification.setVerified(true);
            contactVerification.setLocked(false);
            contactVerification.setLockReason(null);
            contactVerification.setLockedAt(null);
            contactVerification.setResendAttempt(0);
            contactVerification.setVerificationAttempt(0);
            contactVerification.setExpiryDate(LocalDateTime.now());
            contactVerificationRepository.save(contactVerification);
        } else {
            checkVerificationAttemptsAndGenerateException(contactVerification, deviceType);
        }
    }

    private void lock(String contact, ContactVerification contactVerification, LockReason lockedReason) {
        contactVerification.setLocked(true);
        contactVerification.setLockReason(lockedReason);
        contactVerification.setLockedAt(LocalDateTime.now());

        contactVerificationRepository.save(contactVerification);

        logger.info("Verification of contact {}  is locked because {}.", contact, lockedReason);
    }

    private void unlock(String contact, ContactVerification contactVerification) {
        contactVerification.setLocked(false);
        contactVerification.setLockedAt(null);
        contactVerification.setLockReason(null);
        contactVerification.setResendAttempt(0);
        contactVerification.setVerificationAttempt(0);

        contactVerificationRepository.save(contactVerification);

        logger.info("Verification of contact {}  is unlocked.", contact);
    }

    public LockReason getLockReason(DeviceType deviceType) {
        if (DeviceType.EMAIL.equals(deviceType)) {
            return LockReason.EMAIL_VERIFICATION_ATTEMPT_LIMIT_EXCEEDED;
        }

        return LockReason.PHONE_VERIFICATION_ATTEMPT_LIMIT_EXCEEDED;
    }

    private void incrementVerificationAttempt(ContactVerification contactVerification) {
        //todo: automate time from application.yml
        if (contactVerification.isLocked() && LocalDateTime.now().isAfter(contactVerification.getLockedAt().plusMinutes(30))) {
            contactVerification.setVerificationAttempt(1);
            contactVerification.setLocked(false);
            contactVerification.setLockReason(null);
            contactVerification.setLockedAt(null);
        } else {
            contactVerification.setVerificationAttempt(contactVerification.getVerificationAttempt() + 1);
        }

        contactVerificationRepository.save(contactVerification);
    }

    private int getRemainingVerificationAttemptsCount(ContactVerification contactVerification) {
        return Constants.MAX_OTP_VERIFICATION_ATTEMPT - contactVerification.getVerificationAttempt();
    }

    public void checkVerificationAttemptsAndGenerateException(ContactVerification contactVerification, DeviceType deviceType) {
        int attemptsLeft = getRemainingVerificationAttemptsCount(contactVerification);

        LockReason lockReason = getLockReason(deviceType);

        if (attemptsLeft > 0) {
            String errorMessage = messages.get("user.account.invalidCode", String.valueOf(attemptsLeft));

            throw new BadRequestException(errorMessage);
        } else {
            contactVerification.setVerified(false);
            contactVerification.setVerifiedAt(null);
            contactVerificationRepository.save(contactVerification);

            lock(contactVerification.getContact(), contactVerification, lockReason);

            String errorMessage = messages.get("user.otp.attemptLimitExceed", StringUtils.capitalize(deviceType.getType()));

            throw new BadRequestException(errorMessage);
        }
    }
}
