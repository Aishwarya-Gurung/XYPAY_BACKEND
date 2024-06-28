package com.machpay.affiliate.fee.dao;

import com.machpay.affiliate.entity.FeeParameter;
import com.machpay.affiliate.entity.FeeRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FeeRangeRepository extends JpaRepository<FeeRange, Long> {
    List<FeeRange> findAllByFeeParameterAndActive(FeeParameter feeParameter, boolean isActive);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE FeeRange f SET f.active=false WHERE f.feeParameter=:feeParameter")
    void invalidateFeeRange(@Param("feeParameter") FeeParameter feeParameter);
}
