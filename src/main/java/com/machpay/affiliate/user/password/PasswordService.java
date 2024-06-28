package com.machpay.affiliate.user.password;

import com.machpay.affiliate.common.Messages;
import com.machpay.affiliate.user.auth.dto.ForgetPasswordResponse;
import com.machpay.affiliate.user.verification.VerificationResponse;
import com.machpay.affiliate.common.exception.BadRequestException;
import com.machpay.affiliate.common.exception.TokenExpiredException;
import com.machpay.affiliate.entity.ResetPassword;
import com.machpay.affiliate.entity.Sender;
import com.machpay.affiliate.entity.User;
import com.machpay.affiliate.security.UserPrincipal;
import com.machpay.affiliate.user.sender.SenderService;
import com.machpay.affiliate.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordService.class);

    @Autowired
    private Messages messages;

    @Autowired
    private ResetPasswordRepository resetPasswordRepository;

    @Autowired
    private SenderService senderService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResetPassword create(Sender sender) {
        ResetPassword previousRequest =
                resetPasswordRepository.findByUserIdAndExpiryDateIsAfter(sender.getId(), LocalDateTime.now());
        if (previousRequest != null) {
            previousRequest.setExpiryDate(LocalDateTime.now());
        }

        ResetPassword resetPassword = new ResetPassword();

        resetPassword.setUser(sender);
        resetPassword.setToken(UUID.randomUUID().toString());
        resetPassword.setExpiryDate(LocalDateTime.now().plusMinutes(30L));
        resetPassword.setResetAttempt(0);
        return resetPasswordRepository.save(resetPassword);
    }

    public ForgetPasswordResponse handleForgetPassword(String email) {
        if (!userService.isPresent(email)) {
            logger.error("Reset password requested for an account with an email {} which is not registered.", email);

            ForgetPasswordResponse forgetPasswordResponse = new ForgetPasswordResponse();
            forgetPasswordResponse.setMessage(messages.get("user.account.passwordResetLinkIsNotSent", email));

            return forgetPasswordResponse;
        }

        if (userService.isLocked(email)) {
            logger.error("Reset password requested for an account with an email {} which is locked.", email);

            throw new BadRequestException(messages.get("user.account.unableToResetPassword"));
        }

        sendResetPasswordMail(email);

        ForgetPasswordResponse forgetPasswordResponse = new ForgetPasswordResponse();
        forgetPasswordResponse.setMessage(messages.get("user.account.passwordResetLinkSent", email));

        return forgetPasswordResponse;
    }

    public void sendResetPasswordMail(String email) {
        Sender sender = senderService.findByEmail(email);
        ResetPassword resetPassword = create(sender);
    }

    public VerificationResponse resetPassword(String recoveryToken, String newPassword) {
        ResetPassword resetPassword = resetPasswordRepository.findByToken(recoveryToken);
        VerificationResponse verificationResponse = new VerificationResponse();

        if (resetPassword == null) {
            throw new TokenExpiredException("Invalid password reset link. Please send a new request.");
        }
        if (resetPassword.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Your password reset link has expired. Please send a new request.");
        }

        int resetAttempt = resetPassword.getResetAttempt() + 1;
        resetPassword.setResetAttempt(resetAttempt);
        User user = resetPassword.getUser();
        userService.resetPassword(user, newPassword);
        resetPassword.setExpiryDate(LocalDateTime.now());
        resetPasswordRepository.save(resetPassword);

        verificationResponse.setStatus(true);
        verificationResponse.setMessage("Your password has been reset. Please sign in");

        return verificationResponse;
    }

    public VerificationResponse resetPassword(UserPrincipal userPrincipal, String newPassword,
                                              String oldPassword) {
        VerificationResponse verificationResponse = new VerificationResponse();
        User user = userService.findById(userPrincipal.getId());
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Your old password does not match");
        }
        userService.resetPassword(user, newPassword);

        verificationResponse.setStatus(true);
        verificationResponse.setMessage("Your password has been reset");

        return verificationResponse;
    }
}
