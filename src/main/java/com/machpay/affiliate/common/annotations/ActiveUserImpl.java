package com.machpay.affiliate.common.annotations;

import com.machpay.affiliate.user.sender.SenderService;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Configuration
public class ActiveUserImpl {

    @Autowired
    private SenderService senderService;

    @Before("@annotation(com.machpay.affiliate.common.annotations.ActiveUser)")
    public void before() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();

        senderService.checkAccountDeleteRequestStatus(request);
    }
}