package com.machpay.affiliate.user.sender;

import com.machpay.affiliate.common.ListResponse;
import com.machpay.affiliate.common.enums.SenderSearchOption;
import com.machpay.affiliate.entity.Sender;
import com.machpay.affiliate.security.CurrentUser;
import com.machpay.affiliate.security.UserPrincipal;
import com.machpay.affiliate.user.sender.dto.DeletionRequestedSenderResponse;
import com.machpay.affiliate.user.sender.dto.FilterLockedSenderRequest;
import com.machpay.affiliate.user.sender.dto.KYCInfo;
import com.machpay.affiliate.user.sender.dto.KycStatusRequest;
import com.machpay.affiliate.user.sender.dto.LockedSenderFilterRequest;
import com.machpay.affiliate.user.sender.dto.PaginatedLockedSenderResponse;
import com.machpay.affiliate.user.sender.dto.StatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/senders")
public class SenderController {

    @Autowired
    private SenderService senderService;

    @Autowired
    private SenderMapper senderMapper;

    @GetMapping("")
    @PreAuthorize("hasRole('USER')")
    public MappingJacksonValue getCurrentSender(@CurrentUser UserPrincipal userPrincipal) {
        return senderService.getCurrentSender(userPrincipal.getEmail());
    }

    @PostMapping("/kyc-status")
    @PreAuthorize("hasRole('USER')")
    public StatusResponse updateKYCStatus(@CurrentUser UserPrincipal userPrincipal,
                                          @Valid @RequestBody KycStatusRequest kycStatusRequest) {
        Sender sender = senderService.updateKYCStatus(userPrincipal, kycStatusRequest.getStatus());

        return senderMapper.toStatusResponse(sender);
    }

    @PatchMapping("/sender-detail")
    @PreAuthorize("hasRole('USER')")
    public MappingJacksonValue updateSenderDetail(@CurrentUser UserPrincipal userPrincipal,
                                                  @Valid @RequestBody KYCInfo kycInfo) {
        return senderService.updateSenderDetail(userPrincipal.getId(), kycInfo);
    }

    @PatchMapping("/kyc-info")
    @PreAuthorize("hasRole('USER')")
    public KYCInfo updateKYCInfo(@CurrentUser UserPrincipal userPrincipal,
                                 @Valid @RequestBody KYCInfo kycInfo) {
        return senderService.updateKYCInfo(userPrincipal.getId(), kycInfo);
    }

    @GetMapping("/account-deletion-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ListResponse accountDeletionSendersList() {
        List<DeletionRequestedSenderResponse> senderResponses = senderService.getAccountDeletionRequestedSenders();

        return new ListResponse(senderResponses);
    }

    @PostMapping("/revert-account-deletion/{senderReferenceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void revertAccountDeletion(@PathVariable("senderReferenceId") String senderReferenceId,
                                      @RequestBody String revertReason
                                      ) {
        senderService.revertAccountDeletion(senderReferenceId, revertReason);
    }

    @GetMapping("/locked")
    @PreAuthorize("hasRole('ADMIN')")
    public ListResponse lockedSendersList(@RequestParam(value = "page") Integer page,
                                          @RequestParam(value = "page-size") Integer pageSize) {
        PaginatedLockedSenderResponse lockedSenderResponse =
                senderService.getLockedSenders(page, pageSize);

        return new ListResponse(lockedSenderResponse.getSenderResponseList(), lockedSenderResponse.getPaging());
    }

    @PostMapping("/locked/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ListResponse filterLockedSenders(
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "page-size") Integer pageSize,
            @RequestParam(value = "search-by") String searchOption,
            @Valid @RequestBody LockedSenderFilterRequest lockedSenderFilterRequest) {
        FilterLockedSenderRequest request = new FilterLockedSenderRequest();
        request.setPage(page);
        request.setPageSize(pageSize);
        request.setSearchOption(SenderSearchOption.get(searchOption));
        request.setLockedSenderFilterRequest(lockedSenderFilterRequest);

        PaginatedLockedSenderResponse lockedSenderResponse = senderService.filterLockedSenders(request);

        return new ListResponse(lockedSenderResponse.getSenderResponseList(), lockedSenderResponse.getPaging());
    }

    @GetMapping("/{senderReferenceId}/unlock")
    @PreAuthorize("hasRole('ADMIN')")
    public void unLock(@PathVariable("senderReferenceId") String senderReferenceId) {
        senderService.unLock(senderReferenceId);
    }

    @DeleteMapping("")
    @PreAuthorize("hasRole('USER')")
    public void delete(@CurrentUser UserPrincipal userPrincipal) {
        senderService.requestDelete(userPrincipal.getId());
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/privacy-policy/accept")
    public void privacyPolicy(@CurrentUser UserPrincipal userPrincipal, HttpServletRequest request) {
        senderService.acceptPrivacyPolicy(userPrincipal.getId(), request);
    }

    @PatchMapping("/provider")
    @PreAuthorize("hasRole('USER')")
    public void updateProvider(@CurrentUser UserPrincipal userPrincipal) {
        senderService.updateProvider(userPrincipal.getId());
    }
}