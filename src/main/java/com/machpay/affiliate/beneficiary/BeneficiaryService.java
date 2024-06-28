package com.machpay.affiliate.beneficiary;

import com.machpay.affiliate.address.AddressMapper;
import com.machpay.affiliate.address.AddressRepository;
import com.machpay.affiliate.address.AddressResponse;
import com.machpay.affiliate.beneficiary.bank.BeneficiaryBankService;
import com.machpay.affiliate.beneficiary.bank.dto.BeneficiaryBankResponse;
import com.machpay.affiliate.beneficiary.dto.BeneficiaryRequest;
import com.machpay.affiliate.beneficiary.dto.BeneficiaryResponse;
import com.machpay.affiliate.common.Messages;
import com.machpay.affiliate.common.enums.KYCStatus;
import com.machpay.affiliate.common.exception.BadRequestException;
import com.machpay.affiliate.entity.Address;
import com.machpay.affiliate.entity.Beneficiary;
import com.machpay.affiliate.entity.Sender;
import com.machpay.affiliate.user.sender.SenderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.Collectors;

@Service
public class BeneficiaryService {

    private static final Logger logger = LoggerFactory.getLogger(BeneficiaryService.class);

    @Autowired
    private SenderService senderService;

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private BeneficiaryBankService beneficiaryBankService;

    @Autowired
    private BeneficiaryMapper beneficiaryMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private Messages messages;

    public static String createFullName(Beneficiary beneficiary) {
        return Stream
                .of(beneficiary.getFirstName(), beneficiary.getMiddleName(), beneficiary.getLastName())
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(" "));
    }

    @Transactional
    public BeneficiaryResponse create(Long senderId, BeneficiaryRequest beneficiaryRequest) {
        Sender sender = senderService.findById(senderId);

        if (!sender.isPrivacyPolicyAccepted()) {
            String errorMessage = messages.get("affiliate.privacyPolicy");

            throw new BadRequestException(errorMessage);
        }

        if (senderService.isMigrated(sender)) {
            String errorMessage = messages.get("user.migrated");

            throw new BadRequestException(errorMessage);
        }

        if (!sender.getKycStatus().equals(KYCStatus.VERIFIED)) {
            throw new BadRequestException("Sender KYC is not verified.");
        }

        Beneficiary beneficiary = buildBeneficiary(sender, beneficiaryRequest);
        beneficiaryRepository.save(beneficiary);

        return buildBeneficiaryResponse(beneficiary);
    }

    @Transactional
    public Beneficiary buildBeneficiary(Sender sender, BeneficiaryRequest beneficiaryInfo) {
        Beneficiary beneficiary = beneficiaryMapper.toBeneficiary(beneficiaryInfo);
        Address address = addressMapper.toAddress(beneficiaryInfo);

        addressRepository.save(address);
        beneficiary.setSender(sender);
        beneficiary.setAddress(address);
        beneficiary.setReferenceId(UUID.randomUUID());
        beneficiary.setIsCashPickupEnabled(true);


        return beneficiary;
    }

    @Transactional
    public Beneficiary update(Long senderId, UUID referenceId, BeneficiaryRequest beneficiaryRequest) {
        Sender sender = senderService.findById(senderId);

        if (!sender.isPrivacyPolicyAccepted()) {
            String errorMessage = messages.get("affiliate.privacyPolicy");

            throw new BadRequestException(errorMessage);
        }

        if (senderService.isMigrated(sender)) {
            String errorMessage = messages.get("user.migrated");

            throw new BadRequestException(errorMessage);
        }

        Beneficiary beneficiary = findByReferenceId(referenceId);
        beneficiary = buildBeneficiary(beneficiary, beneficiaryRequest);

        return beneficiaryRepository.save(beneficiary);
    }

    @Transactional
    public Beneficiary enableCashPickup(Long senderId, UUID referenceId, Boolean status) {
        Sender sender = senderService.findById(senderId);
        Beneficiary beneficiary = findByReferenceId(referenceId);
        beneficiary.setIsCashPickupEnabled(status);
        BeneficiaryRequest beneficiaryRequest = beneficiaryMapper.toBeneficiaryRequest(beneficiary);
        beneficiary = buildBeneficiary(beneficiary, beneficiaryRequest);

        return beneficiaryRepository.save(beneficiary);
    }

    @Transactional
    public Beneficiary buildBeneficiary(Beneficiary beneficiary, BeneficiaryRequest beneficiaryRequest) {
        Address address = addressMapper.toAddress(beneficiaryRequest);
        addressRepository.save(address);
        beneficiary.setAddress(address);
        beneficiaryMapper.toBeneficiary(beneficiaryRequest, beneficiary);

        return beneficiary;
    }

    public List<BeneficiaryResponse> getBeneficiaryList(Long senderId) {
        Sender sender = senderService.findById(senderId);
        List<Beneficiary> beneficiaries = beneficiaryRepository.findAllBySenderAndActiveTrueOrderByCreatedAtDesc(sender);

        return beneficiaries.stream().map(this::buildBeneficiaryResponse).collect(Collectors.toList());
    }

    public Beneficiary findByReferenceId(UUID referenceId) {
        return beneficiaryRepository.findBeneficiaryByReferenceId(referenceId).orElseThrow(() -> new BadRequestException(
                "Beneficiary not found with id : " + referenceId));
    }

    private BeneficiaryResponse buildBeneficiaryResponse(Beneficiary beneficiary) {
        AddressResponse addressResponse = addressMapper.toAddressResponse(beneficiary);
        List<BeneficiaryBankResponse> beneficiaryBankRespons =
                beneficiaryBankService.getBeneficiaryAccountResponseList(beneficiary);
        BeneficiaryResponse beneficiaryResponse = beneficiaryMapper.toBeneficiaryResponse((beneficiary));
        beneficiaryResponse.setAddress(addressResponse);
        beneficiaryResponse.setBanks(beneficiaryBankRespons);
        beneficiaryResponse.setEditable(true);
        beneficiaryResponse.setDeletable(true);

        return beneficiaryResponse;
    }

    public void delete(Long userId, UUID referenceId) {
        Sender sender = senderService.findById(userId);
        Beneficiary beneficiary = findByReferenceId(referenceId);

            beneficiary.setActive(false);

            beneficiaryRepository.save(beneficiary);

            logger.info("Beneficiary with reference id [{}] is removed by user [{}]", beneficiary.getReferenceId(),
                    sender.getEmail());
    }

    public Beneficiary save(Beneficiary beneficiary) {
        return beneficiaryRepository.save(beneficiary);
    }
}
