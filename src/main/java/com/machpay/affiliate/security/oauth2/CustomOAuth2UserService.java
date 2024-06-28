package com.machpay.affiliate.security.oauth2;

import com.machpay.affiliate.common.enums.AuthProvider;
import com.machpay.affiliate.common.enums.RoleType;
import com.machpay.affiliate.common.exception.OAuth2AuthenticationProcessingException;
import com.machpay.affiliate.entity.Role;
import com.machpay.affiliate.entity.Sender;
import com.machpay.affiliate.security.UserPrincipal;
import com.machpay.affiliate.security.oauth2.user.OAuth2UserInfo;
import com.machpay.affiliate.security.oauth2.user.OAuth2UserInfoFactory;
import com.machpay.affiliate.user.role.RoleService;
import com.machpay.affiliate.user.sender.SenderRepository;
import com.machpay.affiliate.user.sender.SenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private SenderRepository senderRepository;

    @Autowired
    private SenderService senderService;

    @Autowired
    private RoleService roleService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo =
                OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId()
                        , oAuth2User.getAttributes());
        if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        Optional<Sender> senderOptional = senderRepository.findByEmail(oAuth2UserInfo.getEmail());
        Sender sender;
        if (senderOptional.isPresent()) {
            sender = senderOptional.get();
            if (!sender.getProvider().equals(AuthProvider.get(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException(String.format("Looks like you're signed up with %s " +
                        "account. Please use your %s account to login.", sender.getProvider(), sender.getProvider()));
            }
            sender = updateExistingSender(sender, oAuth2UserInfo);
        } else {
            sender = registerNewSender(oAuth2UserRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create(sender, oAuth2User.getAttributes());
    }

    private Sender registerNewSender(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        Sender sender = new Sender();
        Role roleUser = roleService.findByName(RoleType.ROLE_GUEST);

        sender.setProvider(AuthProvider.get(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        sender.setProviderId(oAuth2UserInfo.getId());
        sender.setFirstName(oAuth2UserInfo.getFirstName());
        sender.setMiddleName(oAuth2UserInfo.getMiddleName());
        sender.setLastName(oAuth2UserInfo.getLastName());
        sender.setEmail(oAuth2UserInfo.getEmail());
        sender.setImageUrl(oAuth2UserInfo.getImageUrl());
        sender.setRoles(new ArrayList<>(Collections.singletonList(roleUser)));
        sender.setEmailVerified(true);

        return senderService.save(sender);
    }

    private Sender updateExistingSender(Sender existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setFirstName(oAuth2UserInfo.getFirstName());
        existingUser.setMiddleName(oAuth2UserInfo.getMiddleName());
        existingUser.setLastName(oAuth2UserInfo.getLastName());
        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());

        return senderService.save(existingUser);
    }

}