package com.machpay.affiliate.user;

import com.machpay.affiliate.common.enums.LockReason;
import com.machpay.affiliate.common.exception.ResourceNotFoundException;
import com.machpay.affiliate.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("user", "email",
                email));
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("user", "id", id));
    }

    public void resetPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    public Integer getLoginAttempts(String email) {
        return userRepository.getLoginAttempts(email);
    }

    public void lock(String email, LockReason lockReason) {
        User user = findByEmail(email);
        user.setLocked(true);
        user.setLockReason(lockReason);
        save(user);

        logger.info("User with email id {} and provider {} is locked because {}", email, user.getProvider(), lockReason.name());
    }

    public void unLock(User user) {
        user.setLocked(false);
        user.setLockReason(null);
        userRepository.save(user);
    }

    public boolean isPresent(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean isLocked(String email) {
        return findByEmail(email).isLocked();
    }
}
