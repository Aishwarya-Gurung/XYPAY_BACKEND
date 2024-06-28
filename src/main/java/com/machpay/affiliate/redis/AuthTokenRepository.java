package com.machpay.affiliate.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthTokenRepository extends CrudRepository<AuthToken, String> {
    Optional<AuthToken> findByReferenceToken(String referenceToken);

    List<AuthToken> findAllByDeviceFingerprint(String deviceFingerprint);

    List<AuthToken> findAllByUserId(Long userId);
}
