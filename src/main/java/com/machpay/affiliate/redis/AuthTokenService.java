package com.machpay.affiliate.redis;

import com.machpay.affiliate.common.Constants;
import com.machpay.affiliate.common.exception.ResourceNotFoundException;
import com.machpay.affiliate.entity.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthTokenService {

    @Autowired
    private AuthTokenRepository authTokenRepository;

    private Logger logger = LoggerFactory.getLogger(AuthTokenService.class);

    public String create(String accessToken, Long userId, Device device) {
        AuthToken authToken = new AuthToken();
        String referenceToken = createReferenceToken();

        authToken.setUserId(userId);
        authToken.setJWTtoken(accessToken);
        authToken.setReferenceToken(referenceToken);
        authToken.setDeviceFingerprint(device.getFingerprint());
        authToken.setDeviceVerified(device.isDeviceVerified());
        logger.info("Creating auth token for userId: {}", userId);

        return authTokenRepository.save(authToken).getReferenceToken();
    }

    public AuthToken getAuthToken(String referenceToken) {
        return authTokenRepository.findByReferenceToken(referenceToken)
                .orElseThrow(() -> new ResourceNotFoundException("AuthToken", "reference_token", referenceToken));
    }

    public List<AuthToken> getAuthTokenByDeviceFingerprint(String fingerprint) {
        return authTokenRepository.findAllByDeviceFingerprint(fingerprint);
    }

    public String getJWTtoken(String referenceToken) {
        Optional<AuthToken> authToken = authTokenRepository.findByReferenceToken(referenceToken);

        // returning null since JWT unauthorised case is handled in token filter class
        // for accessing unauthoriesd resources if token isn't available
        return authToken.isPresent() ? authToken.get().getJWTtoken() : null;
    }

    @Transactional
    public void deleteAuthTokenByUserId(Long userId) {
        List<AuthToken> authToken = authTokenRepository.findAllByUserId(userId);

        if (!authToken.isEmpty()) {
            authTokenRepository.deleteAll(authToken);
        }
    }

    public void deleteAuthTokenByReferenceToken(String referenceToken) {
        Optional<AuthToken> authToken = authTokenRepository.findByReferenceToken(referenceToken);

        if (authToken.isPresent()) {
            logger.info("Deleting authToken with referenceId: {}", referenceToken);
            authTokenRepository.delete(authToken.get());
        } else {
            logger.error("Auth token not found with referenceId: {}", referenceToken);

            throw new ResourceNotFoundException("AuthToken", "reference_token", referenceToken);
        }
    }

    public void deleteAuthTokenByDeviceFingerprint(String fingerprint) {
        List<AuthToken> authToken = getAuthTokenByDeviceFingerprint(fingerprint);
        authTokenRepository.deleteAll(authToken);
    }

    private String createReferenceToken() {
        return UUID.randomUUID().toString();
    }

    public void updateDeviceVerificationStatus(String referenceToken) {
        AuthToken authToken = getAuthToken(referenceToken);
        authToken.setDeviceVerified(true);
        authTokenRepository.save(authToken);
    }

    public void removeInactiveTokens() {
        authTokenRepository.findAll().forEach(this::deleteInactiveToken);
    }

    private void deleteInactiveToken(AuthToken authToken) {
        LocalDateTime timeToDeleteToken = LocalDateTime.now().minusMinutes(Constants.INACTIVE_ACCESS_TOKEN_DELETE_TIME);

        if (authToken.getUpdatedAt().isBefore(timeToDeleteToken)) {
            deleteAuthTokenByReferenceToken(authToken.getReferenceToken());
        }
    }
}
