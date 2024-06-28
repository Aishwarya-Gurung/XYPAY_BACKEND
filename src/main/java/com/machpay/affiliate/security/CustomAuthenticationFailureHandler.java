package com.machpay.affiliate.security;

import com.machpay.affiliate.common.Constants;
import com.machpay.affiliate.common.Messages;
import com.machpay.affiliate.common.enums.AuthProvider;
import com.machpay.affiliate.entity.User;
import com.machpay.affiliate.user.UserService;
import com.machpay.affiliate.user.auth.dto.LoginRequest;
import com.machpay.affiliate.util.HttpServletRequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("authenticationFailureHandler")
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private Messages messages;

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
                                        final AuthenticationException exception) throws IOException, ServletException {
        String error = exception.getMessage();

//        Authprovider not required for now.
//        AuthProvider provider = getProvider(request);

        if (error.equalsIgnoreCase("Bad credentials")) {
//            Disable Social login for now
//            if (provider.equals(AuthProvider.FACEBOOK) || provider.equals(AuthProvider.GOOGLE)) {
//                error = messages.get("user.account.pleaseProceedWithSocialLogin", provider.getProvider());
//            } else {
            Integer loginAttempts = getRemainingLoginAttempts(request);
            error = (loginAttempts > 0)
                    ? messages.get("user.account.badCredentials", loginAttempts.toString())
                    : messages.get("user.account.locked");
//            }
        }

        if (error.contains("user not found with email")) {
            error = messages.get("user.account.userNotFoundWithGivenEmail");
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, error);
    }

    private AuthProvider getProvider(HttpServletRequest request) {
        byte[] bytes = HttpServletRequestUtils.getRequestReaderByte(request);
        String email = HttpServletRequestUtils.getAuthRequest(bytes).getEmail();
        User user = userService.findByEmail(email);

        return user.getProvider();
    }

    private Integer getRemainingLoginAttempts(HttpServletRequest request) {
        byte[] bytes = HttpServletRequestUtils.getRequestReaderByte(request);
        LoginRequest authRequest = HttpServletRequestUtils.getAuthRequest(bytes);

        return Constants.MAX_LOGIN_LIMIT - userService.getLoginAttempts(authRequest.getEmail());
    }
}
