package com.machpay.affiliate.security;

import com.machpay.affiliate.common.Constants;
import com.machpay.affiliate.common.enums.LockReason;
import com.machpay.affiliate.entity.User;
import com.machpay.affiliate.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginAttemptService {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(LoginAttemptService.class);

    public void loginSucceeded(String email) {
        User user = userService.findByEmail(email);
        user.setLoginAttempts(0);
        userService.save(user);
        logger.info("User {} logged-in successfully on {}.", user.getEmail(), LocalDateTime.now());
    }

    public void loginFailed(String email) {
        User user = userService.findByEmail(email);
//        Disable social login for now
//        if (!AuthProvider.FACEBOOK.equals(user.getProvider()) && !AuthProvider.GOOGLE.equals(user.getProvider())) {
        user.setLoginAttempts(user.getLoginAttempts() + 1);
        userService.save(user);
        lockIfUserHasMaxLoginAttempts(email);
        logger.info("User {} failed to login with attempt count {} on {}.",
                user.getEmail(), user.getLoginAttempts(), LocalDateTime.now());
//        }
    }

    public boolean isLocked(String email) {
        User user = userService.findByEmail(email);
        return user.isLocked();
    }

    private void lockIfUserHasMaxLoginAttempts(String email) {
        User user = userService.findByEmail(email);
        if (user.getLoginAttempts() >= Constants.MAX_LOGIN_LIMIT) {
            logger.info("User {} locked because {}", user.getEmail(), LockReason.MAX_LOGIN_LIMIT_EXCEEDED);
            userService.lock(email, LockReason.MAX_LOGIN_LIMIT_EXCEEDED);
        }
    }
}
