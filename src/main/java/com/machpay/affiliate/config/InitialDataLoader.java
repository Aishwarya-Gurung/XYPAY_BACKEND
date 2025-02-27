package com.machpay.affiliate.config;

import com.machpay.affiliate.common.enums.AdminCredentials;
import com.machpay.affiliate.common.enums.AuthProvider;
import com.machpay.affiliate.common.enums.PrivilegeType;
import com.machpay.affiliate.common.enums.RoleType;
import com.machpay.affiliate.entity.Admin;
import com.machpay.affiliate.entity.Privilege;
import com.machpay.affiliate.entity.Role;
import com.machpay.affiliate.user.admin.AdminRepository;
import com.machpay.affiliate.user.privilege.PrivilegeRepository;
import com.machpay.affiliate.user.role.RoleRepository;
import com.machpay.affiliate.user.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

     private boolean alreadySetup = false;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup)
            return;

        Privilege readPrivilege = createPrivilegeIfNotFound(PrivilegeType.ROLE_READ);
        Privilege writePrivilege = createPrivilegeIfNotFound(PrivilegeType.ROLE_WRITE);

        List<Privilege> readWritePrivilege = Arrays.asList(readPrivilege, writePrivilege);

        createRoleIfNotFound(RoleType.ROLE_ADMIN, readWritePrivilege);
        createRoleIfNotFound(RoleType.ROLE_USER, readWritePrivilege);
        createRoleIfNotFound(RoleType.ROLE_GUEST, readWritePrivilege);

        Role adminRole = roleService.findByName(RoleType.ROLE_ADMIN);
        createAdminIfNotFound(adminRole);

        alreadySetup = true;
    }

    @Transactional
    public Admin createAdminIfNotFound(Role adminRole) {
        Admin admin = new Admin();

        if (!adminRepository.existsByEmail(AdminCredentials.EMAIL.getValue())) {
            admin.setEmail(AdminCredentials.EMAIL.getValue());
            admin.setFirstName(AdminCredentials.FIRST_NAME.getValue());
            admin.setLastName(AdminCredentials.LAST_NAME.getValue());
            admin.setProvider(AuthProvider.SYSTEM);
            admin.setPassword(passwordEncoder.encode(AdminCredentials.PASSWORD.getValue()));
            admin.setRoles(new ArrayList<>(Collections.singletonList(adminRole)));
            adminRepository.save(admin);
        }

        return admin;
    }

    @Transactional
    public Privilege createPrivilegeIfNotFound(PrivilegeType name) {
        Privilege privilege = privilegeRepository.findByName(name);

        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }

        return privilege;
    }

    @Transactional
    public void createRoleIfNotFound(RoleType name, Collection<Privilege> privileges) {
        if (!roleService.existsByName(name)) {
            Role role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
    }
}