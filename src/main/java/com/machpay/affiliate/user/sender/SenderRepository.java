package com.machpay.affiliate.user.sender;

import com.machpay.affiliate.common.enums.LockReason;
import com.machpay.affiliate.entity.Sender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SenderRepository extends JpaRepository<Sender, Long> {

    Optional<Sender> findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsByPhoneNumber(String phoneNumber);

    Boolean existsByEmailAndEmailVerifiedIsTrue(String email);

    Boolean existsByPhoneNumberAndPhoneNumberVerifiedIsTrue(String phoneNumber);

    Boolean existsByPhoneNumberAndDeletedTrue(String PhoneNumber);

    Boolean existsByEmailAndDeletedTrue(String email);

    Optional<Sender> findByReferenceId(UUID referenceId);

    List<Sender> findAllByAccountDeleteRequestedTrueAndDeletedFalseOrderByAccountDeletionRequestAtDesc();

    @Query("select s from Sender s where s.locked=:locked and s.deleted=:deleted and s.lockReason not in ('SPAM_ACCOUNT')")
    Page<Sender> findAllByLockedTrueAndDeletedFalse(@Param("locked") boolean isLocked,
                                                    @Param("deleted") boolean isDeleted,
                                                    Pageable pageable);

    @Query("select s from Sender s where s.email=:email and s.locked=:locked and s.deleted=:deleted")
    Optional<Sender> findByEmailAndLockedFalseAndDeletedFalse(@Param("email") String email,
                                                              @Param("locked") boolean isLocked,
                                                              @Param("deleted") boolean isDeleted);

    @Query("select s from Sender s where s.locked=:locked and s.lockReason IN :lockedReasons and s.deleted=:deleted")
    Page<Sender> findAllByLockedTrueAndLockReasonAndDeletedFalse(@Param("locked") boolean isLocked,
                                                                 @Param("lockedReasons") List<LockReason> lockedReasons,
                                                                 @Param("deleted") boolean isDeleted,
                                                                 Pageable pageable);


    @Query("SELECT s FROM Sender s WHERE s.KYCVerified = true")
    List<Sender> findAllByKYCVerifiedTrue();
}