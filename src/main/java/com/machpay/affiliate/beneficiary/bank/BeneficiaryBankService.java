package com.machpay.affiliate.beneficiary.bank;

import com.machpay.affiliate.bank.BankService;
import com.machpay.affiliate.beneficiary.BeneficiaryService;
import com.machpay.affiliate.beneficiary.bank.dto.BeneficiaryBankRequest;
import com.machpay.affiliate.beneficiary.bank.dto.BeneficiaryBankResponse;
import com.machpay.affiliate.common.Messages;
import com.machpay.affiliate.common.enums.KYCStatus;
import com.machpay.affiliate.common.exception.BadRequestException;
import com.machpay.affiliate.entity.Beneficiary;
import com.machpay.affiliate.entity.BeneficiaryBank;
import com.machpay.affiliate.entity.Sender;
import com.machpay.affiliate.user.sender.SenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class BeneficiaryBankService {

    @Autowired
    private BeneficiaryBankMapper beneficiaryBankMapper;

    @Autowired
    private BeneficiaryBankRepository beneficiaryBankRepository;

    @Autowired
    private BeneficiaryService beneficiaryService;

    @Autowired
    private SenderService senderService;

    @Autowired
    private BankService bankService;

    @Autowired
    private Messages messages;

    public BeneficiaryBankResponse createBeneficiaryAccount(Long senderId, BeneficiaryBankRequest beneficiaryBankRequest) {
        Sender sender = senderService.findById(senderId);

        if (!sender.isPrivacyPolicyAccepted()) {
            String errorMessage = messages.get("affiliate.privacyPolicy");

            throw new BadRequestException(errorMessage);
        }

        if (!sender.getKycStatus().equals(KYCStatus.VERIFIED)) {
            throw new BadRequestException("Sender KYC is not verified.");
        }

        // Bank name is set to branch location as default branch name
        beneficiaryBankRequest.setBranchLocation(getDefaultBranchLocation(Long.parseLong(beneficiaryBankRequest.getBankId())));
        BeneficiaryBank beneficiaryBank = beneficiaryBankMapper.toBeneficiaryAccount(beneficiaryBankRequest);
        beneficiaryBank.setBeneficiary(beneficiaryService.findByReferenceId(UUID.fromString(beneficiaryBankRequest.getBeneficiaryId())));
        beneficiaryBank.setReferenceId(String.valueOf(UUID.randomUUID()));
        beneficiaryBankRepository.save(beneficiaryBank);
        BeneficiaryBankResponse beneficiaryBankResponse = beneficiaryBankMapper.toBeneficiaryAccountResponse(beneficiaryBank);
        beneficiaryBankResponse.setCurrency(bankService.getPayoutCurrency(beneficiaryBank.getBank()));

        return beneficiaryBankResponse;
    }

    public BeneficiaryBank findByReferenceId(String referenceId) {
        return beneficiaryBankRepository.findByReferenceId(referenceId);
    }

    public List<BeneficiaryBankResponse> getBeneficiaryAccountResponseList(Beneficiary beneficiary) {
        List<BeneficiaryBank> beneficiaryBanks = beneficiaryBankRepository.findByBeneficiary(beneficiary);

        return beneficiaryBanks.stream().map(beneficiaryAccount -> {
            BeneficiaryBankResponse beneficiaryBankResponse = new BeneficiaryBankResponse();

            beneficiaryBankResponse.setBankName(beneficiaryAccount.getBank().getName());
            beneficiaryBankResponse.setAccountNumber(beneficiaryAccount.getAccountNumber());
            beneficiaryBankResponse.setReferenceId(beneficiaryAccount.getReferenceId());
            beneficiaryBankResponse.setAccountType(beneficiaryAccount.getAccountType());
            beneficiaryBankResponse.setCurrency(bankService.getPayoutCurrency(beneficiaryAccount.getBank()));

            return beneficiaryBankResponse;
        }).collect(Collectors.toList());
    }

    private String getDefaultBranchLocation(Long bankId) {
        return bankService.findById(bankId).getName();
    }

    public BeneficiaryBank save(BeneficiaryBank beneficiaryBank) {
        return beneficiaryBankRepository.save(beneficiaryBank);
    }
}
