package com.machpay.affiliate.user.sender;

import com.machpay.affiliate.beneficiary.BeneficiaryService;
import com.machpay.affiliate.common.Messages;
import com.machpay.affiliate.common.Paging;
import com.machpay.affiliate.common.enums.AuthProvider;
import com.machpay.affiliate.common.enums.DTOFilters;
import com.machpay.affiliate.common.enums.DeviceType;
import com.machpay.affiliate.common.enums.Gender;
import com.machpay.affiliate.common.enums.KYCStatus;
import com.machpay.affiliate.common.enums.LockReason;
import com.machpay.affiliate.common.enums.RoleType;
import com.machpay.affiliate.common.enums.SenderSearchOption;
import com.machpay.affiliate.common.enums.ServerSentEvent;
import com.machpay.affiliate.common.exception.BadRequestException;
import com.machpay.affiliate.common.exception.ResourceNotFoundException;
import com.machpay.affiliate.common.exception.ServiceUnavailableException;
import com.machpay.affiliate.common.exception.UnauthorizedAccessException;
import com.machpay.affiliate.config.MailConfig;
import com.machpay.affiliate.device.DeviceService;
import com.machpay.affiliate.entity.Device;
import com.machpay.affiliate.entity.Role;
import com.machpay.affiliate.entity.Sender;
import com.machpay.affiliate.entity.SenderAddress;

import com.machpay.affiliate.entity.State;
import com.machpay.affiliate.entity.User;
import com.machpay.affiliate.redis.AuthToken;
import com.machpay.affiliate.redis.AuthTokenService;
import com.machpay.affiliate.security.UserPrincipal;
import com.machpay.affiliate.senderAddress.SenderAddressMapper;
import com.machpay.affiliate.senderAddress.SenderAddressRepository;
import com.machpay.affiliate.senderAddress.SenderAddressResponse;
import com.machpay.affiliate.serversentevent.ServerSentEventService;
import com.machpay.affiliate.state.StateService;
import com.machpay.affiliate.user.UserService;
import com.machpay.affiliate.user.auth.AuthMapper;
import com.machpay.affiliate.user.auth.dto.Oauth2SignupRequest;
import com.machpay.affiliate.user.auth.dto.SignUpRequest;
import com.machpay.affiliate.user.auth.dto.SignUpRequestV2;
import com.machpay.affiliate.user.role.RoleService;
import com.machpay.affiliate.user.sender.dto.AddressResponse;
import com.machpay.affiliate.user.sender.dto.BasicInfoResponse;
import com.machpay.affiliate.user.sender.dto.DeletionRequestedSenderResponse;
import com.machpay.affiliate.user.sender.dto.FilterLockedSenderRequest;
import com.machpay.affiliate.user.sender.dto.KYCInfo;
import com.machpay.affiliate.user.sender.dto.LockedSenderFilterRequest;
import com.machpay.affiliate.user.sender.dto.PaginatedLockedSenderResponse;
import com.machpay.affiliate.user.sender.dto.SenderAuthResponse;
import com.machpay.affiliate.user.sender.dto.SenderResponse;
import com.machpay.affiliate.user.sender.dto.StatusResponse;
import com.machpay.affiliate.user.verification.ContactVerificationService;
import com.machpay.affiliate.user.verification.TwoFaVerificationService;
import com.machpay.affiliate.util.DTOFilterUtils;
import com.machpay.affiliate.util.DateTimeUtils;
import com.machpay.affiliate.util.HttpServletRequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SenderService {
    private static final Logger logger = LoggerFactory.getLogger(SenderService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SenderRepository senderRepository;

    @Autowired
    private SenderAddressRepository senderAddressRepository;

    @Autowired
    private SenderAddressMapper senderAddressMapper;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private BeneficiaryService beneficiaryService;

    @Autowired
    private SenderMapper senderMapper;

    @Autowired
    private StateService stateService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private TwoFaVerificationService twoFaVerificationService;

    @Autowired
    private ContactVerificationService contactVerificationService;

    @Autowired
    private ServerSentEventService serverSentEventService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private MailConfig mailConfig;

    @Autowired
    private Messages messages;

    public Sender findById(Long id) {
        return senderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sender", "id", id));
    }

    public Sender findByEmail(String email) {
        return senderRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("user", "id", email));
    }

    public Sender findByReferenceId(UUID referenceId) {
        return senderRepository.findByReferenceId(referenceId).orElseThrow(() -> new ResourceNotFoundException(
                "Sender", "referenceId", referenceId));
    }

    public Boolean isPhoneDuplicateAndVerified(String phoneNumber) {
        return senderRepository.existsByPhoneNumberAndPhoneNumberVerifiedIsTrue(phoneNumber);
    }

    public Boolean isEmailDuplicateAndVerified(String email) {
        return senderRepository.existsByEmailAndEmailVerifiedIsTrue(email);
    }

    public Boolean isEmailDuplicate(String email) {
        return senderRepository.existsByEmail(email);
    }

    public Boolean isPhoneDuplicate(String phoneNumber) {
        return senderRepository.existsByPhoneNumber(phoneNumber);
    }

    public Boolean isPhoneNumberDuplicateAndUserDeleted(String phoneNumber) {
        return senderRepository.existsByPhoneNumberAndDeletedTrue(phoneNumber);
    }

    public Boolean isEmailDuplicateAndUserDeleted(String email) {
        return senderRepository.existsByEmailAndDeletedTrue(email);
    }

    public Sender createV2(SignUpRequestV2 signUpRequestV2, HttpServletRequest request) {
        State state = stateService.findByCode(signUpRequestV2.getState());
        Role roleUser = roleService.findByName(RoleType.ROLE_USER);
        Sender sender = senderMapper.toSender(signUpRequestV2);

        sender.setState(state);
        sender.setNewUser(true);
        sender.setPrivacyPolicyAccepted(true);
        sender.setProvider(AuthProvider.SYSTEM);
        sender.setKycStatus(KYCStatus.UNVERIFIED);
        if (signUpRequestV2.getGender() != null) {
            sender.setGender(Gender.valueOf(signUpRequestV2.getGender()));
        }
        sender.setPassword(passwordEncoder.encode(sender.getPassword()));
        sender.setReferenceId(UUID.randomUUID());
        sender.setRoles(new ArrayList<>(Collections.singletonList(roleUser)));

        return save(sender);
    }

    public Sender save(Sender sender) {
        return senderRepository.save(sender);
    }

    public SenderResponse buildSenderResponse(Sender sender) {
        return senderMapper.toSenderResponse(sender);
    }

    public SenderAuthResponse buildSenderAuthResponse(Sender sender) {
        SenderAddress senderAddress = senderAddressRepository.findBySender(sender);
        SenderAddressResponse senderAddressResponse = senderAddressMapper.toSenderAddressResponse(senderAddress);

        SenderAuthResponse senderAuthResponse = senderMapper.toUserResponse(sender);
        StatusResponse statusResponse = senderMapper.toStatusResponse(sender);
        AddressResponse addressResponse = senderMapper.toAddressResponse(sender);
        BasicInfoResponse basicInfoResponse = senderMapper.toBasicInfoResponse(sender);

        basicInfoResponse.setAddress(addressResponse);
        basicInfoResponse.setSenderAddress(senderAddressResponse);
        basicInfoResponse.setRoles(sender.getRoles().stream()
                .map(role -> RoleType.valueOf(role.getName().toString()).toString().split("_")[1])
                .collect(Collectors.toList()));
        senderAuthResponse.setStatus(statusResponse);
        senderAuthResponse.setSender(basicInfoResponse);
        senderAuthResponse.setProvider(sender.getProvider());
        senderAuthResponse.setPrivacyPolicyAccepted(sender.isPrivacyPolicyAccepted());
        senderAuthResponse.setAccountDeleteRequested(sender.isAccountDeleteRequested());

        return senderAuthResponse;
    }

    public MappingJacksonValue getCurrentSender(String email) {
        Sender sender = findByEmail(email);
        Set<String> ignoreFields = new HashSet<>();

        if (!sender.isAccountDeleteRequested()) {
            ignoreFields.add("isAccountDeleteRequested");
        }

        if (sender.isPrivacyPolicyAccepted()) {
            ignoreFields.add("isPrivacyPolicyAccepted");
        }

        SenderAuthResponse senderAuthResponse = buildSenderAuthResponse(sender);

        return DTOFilterUtils.filterSenderAuthResponse(senderAuthResponse, DTOFilters.SENDER_RESPONSE, ignoreFields);
    }

    public Sender updateKYCStatus(UserPrincipal userPrincipal, KYCStatus status) {
        Sender sender = findByEmail(userPrincipal.getEmail());

        if (!sender.getKycStatus().equals(status)) {
            return updateKYCStatus(sender, status);
        }

        return sender;
    }

    public MappingJacksonValue updateSenderDetail(Long senderId, KYCInfo kycInfo) {
        State stateObj = stateService.findByCode(kycInfo.getState());
        Sender sender = findById(senderId);
        Set<String> ignoreFields = getIgnoreFields(sender);

        sender.setState(stateObj);
        sender.setLastName(kycInfo.getLastName());
        sender.setFirstName(kycInfo.getFirstName());
        sender.setMiddleName(kycInfo.getMiddleName());
        sender.setDateOfBirth(kycInfo.getDateOfBirth());
        sender.setGender(Gender.valueOf(kycInfo.getGender()));
        senderRepository.save(sender);

        if (senderAddressRepository.existsBySender(sender)) {
            SenderAddress senderAddress = senderAddressRepository.findBySender(sender);
            senderAddress.setAddressLine1(kycInfo.getAddressLine1());
            senderAddress.setCity(kycInfo.getCity());
            senderAddress.setZipcode(kycInfo.getZipcode());
            senderAddressRepository.save(senderAddress);
        } else {
            SenderAddress senderAddress = senderAddressMapper.toSenderAddress(kycInfo, sender);
            senderAddressRepository.save(senderAddress);
        }

        SenderAuthResponse senderAuthResponse = buildSenderDetailResponse(sender);

        return DTOFilterUtils.filterSenderAuthResponse(senderAuthResponse, DTOFilters.SENDER_RESPONSE,
                ignoreFields);
    }




    public SenderAuthResponse buildSenderDetailResponse(Sender sender) {
        SenderAddress senderAddress = senderAddressRepository.findBySender(sender);
        SenderAddressResponse senderAddressResponse = senderAddressMapper.toSenderAddressResponse(senderAddress);

        SenderAuthResponse senderAuthResponse = senderMapper.toUserResponse(sender);
        StatusResponse statusResponse = senderMapper.toStatusResponse(sender);

        AddressResponse addressResponse = senderMapper.toAddressResponse(sender);
        BasicInfoResponse basicInfoResponse = senderMapper.toBasicInfoResponse(sender);

        basicInfoResponse.setPhoneNumber(getPhoneNumber(sender));
        basicInfoResponse.setAddress(addressResponse);
        basicInfoResponse.setSenderAddress(senderAddressResponse);
        basicInfoResponse.setRoles(sender.getRoles().stream()
                .map(role -> RoleType.valueOf(role.getName().toString()).toString().split("_")[1])
                .collect(Collectors.toList()));
        senderAuthResponse.setStatus(statusResponse);
        senderAuthResponse.setSender(basicInfoResponse);
        senderAuthResponse.setPrivacyPolicyAccepted(sender.isPrivacyPolicyAccepted());
        senderAuthResponse.setAccountDeleteRequested(sender.isAccountDeleteRequested());

        return senderAuthResponse;
    }

    private String getPhoneNumber(Sender sender) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String referenceToken = HttpServletRequestUtils.getReferenceToken(request);
        String fingerPrint = authTokenService.getAuthToken(referenceToken).getDeviceFingerprint();
        Device device = deviceService.getDeviceByUserAndFingerprint(sender, fingerPrint);

        if (!device.isDeviceVerified()) {
            return com.machpay.affiliate.util.StringUtils.maskPhoneNumber(sender.getPhoneNumber());
        }

        return sender.getPhoneNumber();
    }

    public Set<String> getIgnoreFields(Sender sender) {
        Set<String> ignoreFields = new HashSet<>();

        if (!sender.isAccountDeleteRequested()) {
            ignoreFields.add("isDeleteAccountRequested");
        }

        if (sender.isPrivacyPolicyAccepted()) {
            ignoreFields.add("isPrivacyPolicyAccepted");
        }

        return ignoreFields;
    }

    public com.machpay.affiliate.user.sender.dto.KYCInfo updateKYCInfo(Long senderId, com.machpay.affiliate.user.sender.dto.KYCInfo kycInfo) {
        Sender sender = findById(senderId);

        sender.setKYCVerified(true);
        sender.setKycStatus(KYCStatus.VERIFIED);
        senderRepository.save(sender);

        return senderMapper.toKYCInfo(kycInfo);
    }

    public List<DeletionRequestedSenderResponse> getAccountDeletionRequestedSenders() {
        List<Sender> senders = senderRepository.findAllByAccountDeleteRequestedTrueAndDeletedFalseOrderByAccountDeletionRequestAtDesc();

        return senderMapper.toDeletionRequestedSenderResponseList(senders);
    }

    @Transactional
    public void revertAccountDeletion(String referenceId, String reason) {
        Sender sender = findByReferenceId(UUID.fromString(referenceId));

        if (reason.isEmpty()) {
            String errorMessage = messages.get("user.account.revertAccountReasonError");

            throw new BadRequestException(errorMessage);
        }

        if (!sender.isAccountDeleteRequested()) {
            String errorMessage = messages.get("user.account.revertAccountException");

            throw new BadRequestException(errorMessage);

        }

        sender.setAccountDeleteRequested(false);

        sender.setAccountDeletionRevertReason(reason);

        logger.info("Account deletion request reverted for user {}", sender);

        save(sender);
    }

    public String tagPhoneNumberAsDeleted(Sender sender) {
        String email = sender.getEmail();

        //Note: Get email suffix and append the same in Phone Number
        if (sender.isDeleted()) {
            int firstIndex = email.lastIndexOf("_deleted");
            int lastIndex = email.length();
            String emailSuffix = email.substring(firstIndex, lastIndex);

            return sender.getPhoneNumber().concat(emailSuffix);
        }

        return sender.getPhoneNumber();
    }

    private Sender updateKYCStatus(Sender sender, KYCStatus kycStatus) {
        sender.setKYCVerified(KYCStatus.isKycVerifiable(kycStatus));
        sender.setKycStatus(kycStatus);
        senderRepository.save(sender);

        return sender;
    }



    private Paging getPaging(Page<Sender> senders) {
        return new Paging(senders.getNumber(), senders.getSize(), senders.getTotalElements());
    }

    public SenderResponse filterLockedSendersByEmail(LockedSenderFilterRequest request) {
        if (StringUtils.isEmpty(request.getEmail())) {
            throw new BadRequestException("Unable to filter locked senders by EMAIL. Email not provided.");
        }

        Sender sender = senderRepository.findByEmailAndLockedFalseAndDeletedFalse(request.getEmail(), true, false)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        return buildSenderResponse(sender);
    }

    public PaginatedLockedSenderResponse getLockedSenders(Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Sender> senders = senderRepository.findAllByLockedTrueAndDeletedFalse(true, false, pageable);

        return buildPaginatedLockedSenderResponse(senders.getContent(), getPaging(senders));
    }

    public PaginatedLockedSenderResponse filterLockedSenders(FilterLockedSenderRequest request) {
        if (SenderSearchOption.EMAIL.equals(request.getSearchOption())) {

            SenderResponse senderResponse = filterLockedSendersByEmail(request.getLockedSenderFilterRequest());

            List<SenderResponse> senders = new ArrayList<>();
            senders.add(senderResponse);

            return new PaginatedLockedSenderResponse(senders, new Paging(0, 1, (long) senders.size()));
        }

        if (SenderSearchOption.LOCK_REASON.equals(request.getSearchOption())) {
            return filterLockedSendersByLockReason(request);
        }

        throw new BadRequestException("Please provide a valid search filter to filter Locked Senders.");
    }

    public PaginatedLockedSenderResponse filterLockedSendersByLockReason(FilterLockedSenderRequest request) {
        if (request.getLockedSenderFilterRequest().getLockedReasons().isEmpty()) {
            throw new BadRequestException("Unable to filter locked senders by LOCK REASON. Locked Reason not provided.");
        }

        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize());

        Page<Sender> senders = senderRepository.findAllByLockedTrueAndLockReasonAndDeletedFalse(true,
                request.getLockedSenderFilterRequest().getLockedReasons(),
                false, pageable);

        return buildPaginatedLockedSenderResponse(senders.getContent(), getPaging(senders));
    }

    private PaginatedLockedSenderResponse buildPaginatedLockedSenderResponse(List<Sender> senders,
                                                                             Paging paging) {
        List<SenderResponse> senderResponses =
                senders.stream().map(this::buildSenderResponse).collect(Collectors.toList());

        return new PaginatedLockedSenderResponse(senderResponses, paging);
    }

    @Transactional
    public void unLock(String senderReferenceId) {
        User user = findByReferenceId(UUID.fromString(senderReferenceId));
        LockReason lockReason = user.getLockReason();

        switch (lockReason) {
            case MAX_LOGIN_LIMIT_EXCEEDED:
            case SPAM_ACCOUNT:
                user.setLoginAttempts(0);
                break;

            case DEVICE_VERIFICATION_CODE_RESEND_LIMIT_EXCEEDED:
            case DEVICE_VERIFICATION_ATTEMPT_LIMIT_EXCEEDED:
                deviceService.unlockDevice(user, lockReason);
                break;

            case PHONE_VERIFICATION_CODE_RESEND_LIMIT_EXCEEDED:
            case PHONE_VERIFICATION_ATTEMPT_LIMIT_EXCEEDED:
                twoFaVerificationService.resetResendAttempt(user.getId(), DeviceType.PHONE);
                break;

            case EMAIL_VERIFICATION_CODE_RESEND_LIMIT_EXCEEDED:
            case EMAIL_VERIFICATION_ATTEMPT_LIMIT_EXCEEDED:
                twoFaVerificationService.resetResendAttempt(user.getId(), DeviceType.EMAIL);
                break;

            default:
                logger.error("Error occurred while unlocking user[{}] account. Locked reason enum {} is not handled while unlocking",
                        user.getEmail(), lockReason.name());

                throw new ServiceUnavailableException("Unable to unlock the user at the moment. Come" +
                        " back in few minutes and try again");
        }

        userService.unLock(user);
    }

    public void checkAccountDeleteRequestStatus(HttpServletRequest request) {
        String referenceToken = HttpServletRequestUtils.getReferenceToken(request);
        AuthToken authToken = authTokenService.getAuthToken(referenceToken);
        Sender sender = findById(authToken.getUserId());
        String email = sender.getEmail();

        if (sender.isAccountDeleteRequested()) {
            logger.error("User {} who has requested to delete the account is trying to perform some actions.", email);

            String supportEmail = mailConfig.getCustomerSupportEmail();
            String supportPhoneNumber = mailConfig.getCustomerSupportPhoneNumber();
            String errorMessage = messages.get("user.account.deletionRequest", supportEmail, supportPhoneNumber);

            throw new ServiceUnavailableException(errorMessage);
        }
    }

    // Note: This method will send an email to user with steps to delete the account.
    public void requestDelete(Long id) {
        Sender sender = findById(id);
        String email = sender.getEmail();

        if (!sender.isDeleted()) {
            sender.setAccountDeleteRequested(true);
            sender.setAccountDeletionRequestAt(new Date());
            sender.setDeleted(true);
            sender.setDeletedAt(new Date());
            senderRepository.save(sender);

            authTokenService.deleteAuthTokenByUserId(sender.getId());

            return;
        }

        logger.error("User with email {} is already deleted!", email);
    }

    public void acceptPrivacyPolicy(long id, HttpServletRequest request) {
        Sender sender = findById(id);

        if (!sender.isPrivacyPolicyAccepted()) {
            String email = sender.getEmail();
            String date = DateTimeUtils.getCurrentDateTime();

            sender.setPrivacyPolicyAccepted(true);
            save(sender);

            logger.info("User with email {} has accepted the new terms and policies of GIB on {}", email, date);
        }
    }

    // Note: need to refactor when actual delete mechanism is implemented
    public String getEmail(Sender sender) {
        String email = sender.getEmail();

        if (sender.isDeleted()) {
            return email.replaceAll("_deleted[0-9]*", "");
        }

        return email;
    }

    public void checkPhoneVerificationStatus(HttpServletRequest request) {
        String referenceToken = HttpServletRequestUtils.getReferenceToken(request);
        AuthToken authToken = authTokenService.getAuthToken(referenceToken);
        Sender sender = findById(authToken.getUserId());

        if (!sender.isPhoneNumberVerified()) {
            logger.info("User {} tried to access the system with out verifying phone number",
                    authToken.getUserId());
            throw new UnauthorizedAccessException("Phone verification is pending.");
        }
    }

    public void checkEmailVerificationStatus(HttpServletRequest request) {
        String referenceToken = HttpServletRequestUtils.getReferenceToken(request);
        AuthToken authToken = authTokenService.getAuthToken(referenceToken);
        Sender sender = findById(authToken.getUserId());

        if (!sender.isEmailVerified()) {
            logger.info("User {} tried to access the system with out verifying email",
                    authToken.getUserId());
            throw new UnauthorizedAccessException("Email verification is pending.");
        }
    }

    public boolean isMigrated(Sender sender) {
        return AuthProvider.MIGRATED.equals(sender.getProvider());
    }

    public void updateProvider(Long id) {
        Sender sender = findById(id);

        if (isMigrated(sender)) {
            sender.setProvider(AuthProvider.SYSTEM);
            senderRepository.save(sender);

            logger.info("User with email {} has accepted the new terms and conditions of Golden Money Transfer on {}.",
                    sender.getEmail(), DateTimeUtils.getCurrentDateTime());
        }
    }

}