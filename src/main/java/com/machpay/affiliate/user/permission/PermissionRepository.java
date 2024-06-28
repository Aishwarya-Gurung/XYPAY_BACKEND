package com.machpay.affiliate.user.permission;

import com.machpay.affiliate.entity.Permission;
import com.machpay.affiliate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Permission findByUserAndPermission(User user, com.machpay.affiliate.common.enums.Permission name);

    boolean existsByUser(User user);
}
