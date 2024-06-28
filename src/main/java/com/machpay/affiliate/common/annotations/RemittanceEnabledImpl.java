package com.machpay.affiliate.common.annotations;

import com.machpay.affiliate.user.permission.PermissionService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Configuration
public class RemittanceEnabledImpl {

    @Autowired
    private PermissionService permissionService;

    @Before("@annotation(com.machpay.affiliate.common.annotations.RemittanceEnabled)")
    public void before() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();
        permissionService.checkRemittanceEnabled(request);
    }
}
