package com.machpay.affiliate.beneficiary.bank;

import com.machpay.affiliate.beneficiary.bank.dto.BeneficiaryBankRequest;
import com.machpay.affiliate.beneficiary.bank.dto.BeneficiaryBankResponse;
import com.machpay.affiliate.common.annotations.ActiveUser;
import com.machpay.affiliate.security.CurrentUser;
import com.machpay.affiliate.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping("/v1/senders/beneficiaries")
public class BeneficiaryBankController {

    @Autowired
    private BeneficiaryBankService beneficiaryBankService;

    @Autowired
    private BeneficiaryBankMapper beneficiaryBankMapper;

    @ActiveUser
    @PostMapping("/bank")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity addBeneficiaryAccount(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody BeneficiaryBankRequest beneficiaryBankRequest) {
        Long senderId = userPrincipal.getId();
        BeneficiaryBankResponse beneficiaryBankResponse = beneficiaryBankService.createBeneficiaryAccount(senderId, beneficiaryBankRequest);
        return ResponseEntity.ok(beneficiaryBankResponse);

    }

}
