package com.machpay.affiliate.beneficiary.cashpickup;


import com.machpay.affiliate.beneficiary.cashpickup.dto.PayerResponse;
import com.machpay.affiliate.common.exception.ResourceNotFoundException;
import com.machpay.affiliate.entity.CashPickupLocation;
import com.machpay.affiliate.entity.Payer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CashPickupService {

    @Autowired
    private PayerRepository payerRepository;

    @Autowired
    private CashPickupLocationRepository cashPickupLocationRepository;

    public Payer getByReferenceId(Long referenceId) {
        return payerRepository.findByReferenceId(referenceId).orElseThrow(() -> new ResourceNotFoundException("Payer"
                , "referenceId", referenceId));
    }

    public Payer getByCountry(String country) {
        return payerRepository.findByCountry(country).orElseThrow(() -> new ResourceNotFoundException("Payer"
                , "country", country));
    }

    public List<PayerResponse> getAllByCountry(String countryCode) {
        List<Payer> payers = payerRepository.findAllByCountry(countryCode);
        List<PayerResponse> payerResponses = new ArrayList<>();

        payers.forEach(payer -> {
            List<CashPickupLocation> locations = cashPickupLocationRepository.findAllByActiveTrueAndPayer(payer);
            locations.forEach(location -> payerResponses.add(buildPayerResponse(location)));

            if (!cashPickupLocationRepository.existsByPayer(payer)) {
                payerResponses.add(buildPayerResponse(payer));
            }
        });

        return payerResponses;
    }

    private PayerResponse buildPayerResponse(CashPickupLocation location) {
        PayerResponse payerResponse = new PayerResponse();
        payerResponse.setAddress(location.getAddress());
        payerResponse.setName(location.getBranchName());
        payerResponse.setCode(location.getPayer().getCode());
        payerResponse.setCountry(location.getPayer().getCountry());
        payerResponse.setPayingEntity(location.getPayer().getName());
        payerResponse.setReferenceId(location.getPayer().getReferenceId());
        payerResponse.setPhoneNumber(location.getPayer().getPhoneNumber());
        payerResponse.setReceivingCurrency(location.getPayer().getReceivingCurrency());

        return payerResponse;
    }

    private PayerResponse buildPayerResponse(Payer payer) {
        PayerResponse payerResponse = new PayerResponse();
        payerResponse.setAddress(payer.getAddress());
        payerResponse.setCode(payer.getCode());
        payerResponse.setCountry(payer.getCountry());
        payerResponse.setName(payer.getName());
        payerResponse.setReferenceId(payer.getReferenceId());
        payerResponse.setPhoneNumber(payer.getPhoneNumber());
        payerResponse.setReceivingCurrency(payer.getReceivingCurrency());

        return payerResponse;
    }
}
