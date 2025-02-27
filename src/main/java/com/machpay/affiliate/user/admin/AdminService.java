package com.machpay.affiliate.user.admin;

import com.machpay.affiliate.common.enums.RoleType;
import com.machpay.affiliate.common.exception.ResourceNotFoundException;
import com.machpay.affiliate.entity.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AdminMapper adminMapper;

    public Admin findByEmail(String email) {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("admin", "id", email));
    }


    public AdminResponse getCurrentAdmin(String email) {
        Admin admin = findByEmail(email);

        AdminResponse adminResponse = adminMapper.toAdminResponse(admin);
        adminResponse.setRoles(admin.getRoles().stream()
                .map(role -> RoleType.valueOf(role.getName().toString()).toString().split("_")[1])
                .collect(Collectors.toList()));

        return adminResponse;
    }
}