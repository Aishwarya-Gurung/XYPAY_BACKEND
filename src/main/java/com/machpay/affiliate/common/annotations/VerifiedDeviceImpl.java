package com.machpay.affiliate.common.annotations;

import com.machpay.affiliate.device.DeviceService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Configuration
public class VerifiedDeviceImpl {

    @Autowired
    private DeviceService deviceService;

    @Before("@annotation(com.machpay.affiliate.common.annotations.VerifiedDevice)")
    public void before() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        deviceService.checkDeviceStatus(request);
    }
}
