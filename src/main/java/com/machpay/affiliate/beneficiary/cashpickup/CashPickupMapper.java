package com.machpay.affiliate.beneficiary.cashpickup;

import com.machpay.affiliate.beneficiary.cashpickup.dto.PayerResponse;
import com.machpay.affiliate.entity.Payer;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CashPickupMapper {

    PayerResponse toPayerResponse(Payer payer);

    List<PayerResponse> toPayerResponseList(List<Payer> payers);
}
