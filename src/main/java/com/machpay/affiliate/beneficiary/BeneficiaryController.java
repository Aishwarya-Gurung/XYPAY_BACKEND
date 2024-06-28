package com.machpay.affiliate.beneficiary;

import com.machpay.affiliate.beneficiary.dto.BeneficiaryRequest;
import com.machpay.affiliate.beneficiary.dto.BeneficiaryResponse;
import com.machpay.affiliate.common.ListResponse;
import com.machpay.affiliate.common.annotations.ActiveUser;
import com.machpay.affiliate.common.annotations.VerifiedDevice;
import com.machpay.affiliate.common.annotations.VerifiedEmail;
import com.machpay.affiliate.common.annotations.VerifiedPhone;
import com.machpay.affiliate.entity.Beneficiary;
import com.machpay.affiliate.security.CurrentUser;
import com.machpay.affiliate.security.UserPrincipal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;

@RestController
@RequestMapping("/v1/senders/beneficiaries")
public class BeneficiaryController {

    @Autowired
    private BeneficiaryService beneficiaryService;

    @Autowired
    private BeneficiaryMapper beneficiaryMapper;

    @ActiveUser
    @VerifiedPhone
    @VerifiedEmail
    @VerifiedDevice
    @PostMapping("")
    @PreAuthorize("hasRole('USER')")
    public BeneficiaryResponse addBeneficiary(@CurrentUser UserPrincipal userPrincipal,
                                              @Valid @RequestBody BeneficiaryRequest beneficiaryRequest) {
        Long senderId = userPrincipal.getId();

        return beneficiaryService.create(senderId, beneficiaryRequest);
    }

    @ActiveUser
    @VerifiedPhone
    @VerifiedEmail
    @VerifiedDevice
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/update/{referenceId}")
    public BeneficiaryResponse updateBeneficiary(@CurrentUser UserPrincipal userPrincipal,
                                                 @PathVariable("referenceId") UUID referenceId,
                                                 @Valid @RequestBody BeneficiaryRequest beneficiaryRequest) {
        Beneficiary beneficiary = beneficiaryService.update(userPrincipal.getId(), referenceId, beneficiaryRequest);

        return beneficiaryMapper.toBeneficiaryResponse(beneficiary);
    }

    @VerifiedDevice
    @GetMapping("")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity beneficiaryList(@CurrentUser UserPrincipal userPrincipal) {
        Long senderId = userPrincipal.getId();
        List<BeneficiaryResponse> beneficiaryResponses = beneficiaryService.getBeneficiaryList(senderId);

        return ResponseEntity.ok(new ListResponse(beneficiaryResponses));
    }

    @PreAuthorize(("hasRole('USER')"))
    @PutMapping("/{referenceId}/cash-pickup")
    public Beneficiary enableCashPickup(@CurrentUser UserPrincipal userPrincipal,
                                        @PathVariable("referenceId") UUID referenceId,
                                        @RequestParam("enable") Boolean status) {
        return beneficiaryService.enableCashPickup(userPrincipal.getId(), referenceId, status);
    }

    @DeleteMapping("/{referenceId}")
    @PreAuthorize("hasRole('USER')")
    public void delete(@CurrentUser UserPrincipal userPrincipal,
                       @PathVariable("referenceId") UUID referenceId) {
        beneficiaryService.delete(userPrincipal.getId(), referenceId);
    }
}
