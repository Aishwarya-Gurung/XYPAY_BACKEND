package com.machpay.affiliate.user.permission;

import com.machpay.affiliate.common.Messages;
import com.machpay.affiliate.common.enums.Permission;
import com.machpay.affiliate.common.exception.BadRequestException;
import com.machpay.affiliate.entity.Sender;
import com.machpay.affiliate.entity.User;
import com.machpay.affiliate.redis.AuthToken;
import com.machpay.affiliate.redis.AuthTokenService;
import com.machpay.affiliate.user.UserRepository;
import com.machpay.affiliate.user.sender.SenderService;
import com.machpay.affiliate.util.HttpServletRequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

@Service
public class PermissionService {

    @Autowired
    private Messages messages;

    @Autowired
    private SenderService senderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private PermissionRepository permissionRepository;

    public void changePermission(Sender sender, Permission permissionToChange) {
        com.machpay.affiliate.entity.Permission permission = permissionRepository.findByUserAndPermission(sender, permissionToChange);
        permission.setEnabled(!permission.isEnabled());
        permissionRepository.save(permission);
    }

    public void checkRemittanceEnabled(HttpServletRequest request) {
        String referenceToken = HttpServletRequestUtils.getReferenceToken(request);
        AuthToken authToken = authTokenService.getAuthToken(referenceToken);
        Sender sender = senderService.findById(authToken.getUserId());

        com.machpay.affiliate.entity.Permission permission = permissionRepository.findByUserAndPermission(sender, Permission.REMITTANCE);

        if (!permission.isEnabled()) {
            throw new BadRequestException(messages.get("user.transaction.limitExceeded"));
        }
    }

    public Set<com.machpay.affiliate.entity.Permission> createDefaultPermissions(User user) {
        Set<com.machpay.affiliate.entity.Permission> defaultPermissions = new HashSet<>();
        defaultPermissions.add(new com.machpay.affiliate.entity.Permission(user, Permission.REMITTANCE, true));

        return defaultPermissions;
    }

    public void defaultPermissionForOldUser() {
        userRepository.findAll().forEach(user -> {
            if(!permissionRepository.existsByUser(user)) {
                Set<com.machpay.affiliate.entity.Permission> permissions = createDefaultPermissions(user);
                permissionRepository.saveAll(permissions);
            }
        });
    }
}
