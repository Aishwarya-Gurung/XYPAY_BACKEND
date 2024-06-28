package com.machpay.affiliate.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.machpay.affiliate.common.Constants;
import com.machpay.affiliate.common.enums.ClientType;
import com.machpay.affiliate.common.exception.BadRequestException;
import com.machpay.affiliate.user.auth.dto.LoginRequest;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HttpServletRequestUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpServletRequestUtils.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private HttpServletRequestUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static byte[] getRequestReaderByte(HttpServletRequest request) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            IOUtils.copy(request.getReader(), byteArrayOutputStream, "UTF-8");
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            logger.error(Constants.PARSE_ERROR, e);
            throw new InternalAuthenticationServiceException(Constants.PARSE_ERROR, e);
        }
    }

    public static LoginRequest getAuthRequest(byte[] bytes) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            String requestBody = IOUtils.toString(byteArrayInputStream, "UTF-8");

            return objectMapper.readValue(requestBody, LoginRequest.class);
        } catch (IOException e) {
            logger.error(Constants.PARSE_ERROR, e);
            throw new InternalAuthenticationServiceException(Constants.PARSE_ERROR, e);
        }
    }

    public static String getReferenceToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return authHeader.replace("Bearer", "").trim();
    }

    public static LoginRequest getLoginRequest(HttpServletRequest request) {
        byte[] bytes = HttpServletRequestUtils.getRequestReaderByte(request);
        return HttpServletRequestUtils.getAuthRequest(bytes);
    }

    public static String getIPAddress(HttpServletRequest request) {
        String remoteAddress = request.getHeader("X-Forwarded-For");

        if (remoteAddress == null || remoteAddress.isEmpty()) {
            remoteAddress = request.getRemoteAddr();
        }

        return remoteAddress;
    }

    public static boolean isRequestFromWebApp(HttpServletRequest request) {
        String device = request.getHeader("User-Agent");

        return device != null && !device.isEmpty();
    }

    public static boolean isRequestFromNativeApp(HttpServletRequest request) {
        String device = request.getHeader("device");

        if (device == null || device.isEmpty()) return false;

        String[] deviceHeader = HttpServletRequestUtils.parseDeviceHeader(request.getHeader("device"));

        if (deviceHeader.length < 4) {
            logger.error("Device information missing in the request header [{}]", deviceHeader);

            throw new BadRequestException("Device information missing in the request header");
        }

        ClientType clientType = ClientType.valueOf(deviceHeader[3].toUpperCase().trim());

        return ClientType.MOBILE_APP.equals(clientType);
    }

    public static String[] parseDeviceHeader(String device) {
        return device.split("[|]");
    }
}
