package com.machpay.affiliate.user.privilege;

import com.machpay.affiliate.common.enums.PrivilegeType;
import com.machpay.affiliate.entity.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    Privilege findByName(PrivilegeType name);
}
