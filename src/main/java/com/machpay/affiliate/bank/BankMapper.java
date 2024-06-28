package com.machpay.affiliate.bank;

import com.machpay.affiliate.bank.dto.BankResponse;
import com.machpay.affiliate.entity.Bank;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface BankMapper {

    BankResponse toBankResponse(Bank bank);

    @IterableMapping(qualifiedByName = "toBankResponseList")
    List<BankResponse> toBankResponseList(List<Bank> bankList);
}