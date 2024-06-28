package com.machpay.affiliate.security;

import com.machpay.affiliate.common.Messages;
import com.machpay.affiliate.common.exception.AccountDeletedException;
import com.machpay.affiliate.common.exception.AccountLockedException;
import com.machpay.affiliate.entity.Sender;
import com.machpay.affiliate.entity.User;
import com.machpay.affiliate.user.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private Messages messages;

    @Autowired
    private UserService userService;

    @Autowired
    private LoginAttemptService loginAttemptService;

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        User user = userService.findByEmail(email);

        if (!user.isAdmin()) {
            Sender sender = (Sender) user;

            if (sender.isDeleted()) {
                String message = messages.get("user.account.userNotFoundWithGivenEmail");

                logger.info("Login attempt by deleted user with email {}", sender.getEmail());

                throw new AccountDeletedException(message);
            }
        }


        if (loginAttemptService.isLocked(email)) {
            throw new AccountLockedException(messages.get("user.account.locked"));
        }

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userService.findById(id);

        return UserPrincipal.create(user);
    }
}