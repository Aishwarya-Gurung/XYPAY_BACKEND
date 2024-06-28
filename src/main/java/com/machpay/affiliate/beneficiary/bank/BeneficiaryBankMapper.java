package com.machpay.affiliate.beneficiary.bank;

import com.machpay.affiliate.bank.BankService;
import com.machpay.affiliate.beneficiary.bank.dto.BeneficiaryBankRequest;
import com.machpay.affiliate.beneficiary.bank.dto.BeneficiaryBankResponse;
import com.machpay.affiliate.entity.BeneficiaryBank;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = BankService.class)
public interface BeneficiaryBankMapper {

    BeneficiaryBankResponse toBeneficiaryAccountResponse(BeneficiaryBank beneficiaryBank);

    @Mappings({
            @Mapping(source = "bankId", target = "bank")
    })
    BeneficiaryBank toBeneficiaryAccount(BeneficiaryBankRequest beneficiaryBankRequest);
}
