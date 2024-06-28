package com.machpay.affiliate.fee;

import com.machpay.affiliate.common.enums.FundingSource;
import com.machpay.affiliate.common.enums.PaymentMethod;
import com.machpay.affiliate.common.enums.PayoutMethod;
import com.machpay.affiliate.common.exception.BadRequestException;
import com.machpay.affiliate.country.CountryService;
import com.machpay.affiliate.country.currency.CurrencyMapper;
import com.machpay.affiliate.country.currency.CurrencyService;
import com.machpay.affiliate.country.dto.SourceDestinationCountryResponse;
import com.machpay.affiliate.country.sourcedestinationcountry.SourceDestinationCountryMapper;
import com.machpay.affiliate.country.sourcedestinationcountry.SourceDestinationCountryService;
import com.machpay.affiliate.entity.Country;
import com.machpay.affiliate.entity.Currency;
import com.machpay.affiliate.entity.FeeParameter;
import com.machpay.affiliate.entity.FeeRange;
import com.machpay.affiliate.entity.SourceDestinationCountry;
import com.machpay.affiliate.fee.dao.FeeParameterRepository;
import com.machpay.affiliate.fee.dao.FeeRangeRepository;
import com.machpay.affiliate.fee.dto.FeeParameterResponse;
import com.machpay.affiliate.fee.dto.FeeRangeResponse;
import com.machpay.affiliate.fee.dto.FeeRequest;
import com.machpay.affiliate.fee.dto.FeeSetResponse;
import com.machpay.affiliate.fee.mapper.FeeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeeService {
    private static final Logger logger = LoggerFactory.getLogger(FeeService.class);

    @Autowired
    private CountryService countryService;

    @Autowired
    private SourceDestinationCountryService sourceDestinationCountryService;

    @Autowired
    private FeeMapper feeMapper;

    @Autowired
    private CurrencyMapper currencyMapper;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private FeeParameterRepository feeParameterRepository;

    @Autowired
    private FeeRangeRepository feeRangeRepository;

    @Autowired
    private SourceDestinationCountryMapper sourceDestinationCountryMapper;

    @Transactional
    public List<FeeRange> createFees(FeeRequest feeRequest) {
        FeeParameter feeParameter = checkFeeParameter(feeRequest);
        invalidateOldFeeRange(feeParameter);

        List<FeeRange> feeRanges = feeRequest.getFeeRanges().stream().map(feeRangeRequest -> {
            FeeRange feeRange = feeMapper.toFeeRange(feeRangeRequest);
            feeRange.setActive(true);
            feeRange.setFeeParameter(feeParameter);

            return feeRange;
        }).collect(Collectors.toList());

        return feeRangeRepository.saveAll(feeRanges);
    }

    @Transactional
    public FeeParameter checkFeeParameter(FeeRequest feeRequest) {
        SourceDestinationCountry sourceDestinationCountry = countryService
                .getSourceDestinationCountryById(feeRequest.getSourceDestinationCountry());
        Currency currency = currencyService.findByCode(feeRequest.getCurrency());

        if (feeParameterRepository.existsByPaymentMethodAndPayoutMethodAndSourceDestinationCountryAndCurrency(
                feeRequest.getPaymentMethod(), feeRequest.getPayoutMethod(), sourceDestinationCountry, currency)) {
            return feeParameterRepository.findByPaymentMethodAndPayoutMethodAndSourceDestinationCountryAndCurrency(
                    feeRequest.getPaymentMethod(), feeRequest.getPayoutMethod(), sourceDestinationCountry, currency);
        }

        return createFeeParameter(feeRequest.getPaymentMethod(), feeRequest.getPayoutMethod(), sourceDestinationCountry, currency);
    }

    private FeeParameter createFeeParameter(PaymentMethod paymentMethod, PayoutMethod payoutMethod,
                                            SourceDestinationCountry sourceDestinationCountry, Currency currency) {
        FeeParameter feeParameter = new FeeParameter();
        feeParameter.setCurrency(currency);
        feeParameter.setPayoutMethod(payoutMethod);
        feeParameter.setPaymentMethod(paymentMethod);
        feeParameter.setSourceDestinationCountry(sourceDestinationCountry);

        return feeParameterRepository.saveAndFlush(feeParameter);
    }

    public void invalidateOldFeeRange(FeeParameter feeParameter) {
        List<FeeRange> feeRanges = feeRangeRepository.findAllByFeeParameterAndActive(feeParameter, true);

        if (!feeRanges.isEmpty()) {
            feeRangeRepository.invalidateFeeRange(feeParameter);
        }
    }

    public List<FeeParameterResponse> getAllFeeParameter() {
        List<FeeParameter> feeParameters = feeParameterRepository.findAll();

        return feeParameters.stream().map(feeParameter -> {
            SourceDestinationCountryResponse sourceDestinationCountryResponse =
                    getSourceDestinationCountryResponse(feeParameter.getSourceDestinationCountry());
            FeeParameterResponse feeParameterResponse = feeMapper.toFeeParameterResponse(feeParameter);
            feeParameterResponse.setCurrency(currencyMapper.toCurrencyResponse(feeParameter.getCurrency()));
            feeParameterResponse.setSourceDestinationCountry(sourceDestinationCountryResponse);

            return feeParameterResponse;
        }).collect(Collectors.toList());
    }

    private SourceDestinationCountryResponse getSourceDestinationCountryResponse(SourceDestinationCountry sourceDestinationCountry) {
        SourceDestinationCountryResponse sourceDestinationCountryResponse = new SourceDestinationCountryResponse();
        sourceDestinationCountryResponse.setId(sourceDestinationCountry.getId());
        sourceDestinationCountryResponse.setSource(sourceDestinationCountryMapper
                .toSourceDestinationCountry(sourceDestinationCountry.getSourceCountry()));
        sourceDestinationCountryResponse.setDestination(sourceDestinationCountryMapper
                .toSourceDestinationCountry(sourceDestinationCountry.getDestinationCountry()));

        return sourceDestinationCountryResponse;
    }

    public List<FeeSetResponse> getAllFeeSet() {
        List<FeeParameter> feeParameters = feeParameterRepository.findAll();

        List<FeeParameter> filteredFeeParameters = feeParameters.stream()
                .filter(feeParameter -> {
                    List<String> currencies = currencyService.getCurrenciesByCountry(feeParameter.getSourceDestinationCountry()
                            .getDestinationCountry().getThreeCharCode());

                    return currencies.contains(feeParameter.getCurrency().getCode());
                })
                .collect(Collectors.toList());

        return filteredFeeParameters.stream().map(feeParameter -> {
            FeeSetResponse feeSetResponse = getFeeSetResponse(feeParameter);
            feeSetResponse.setFeeRanges(getFeeRangeResponseList(feeParameter));

            return feeSetResponse;
        }).collect(Collectors.toList());
    }

    private FeeSetResponse getFeeSetResponse(FeeParameter feeParameter) {
        FeeSetResponse feeSetResponse = new FeeSetResponse();
        FeeSetResponse.Country sourceCountry = feeMapper.toFeeSetCountryResponse(feeParameter.getSourceDestinationCountry()
                .getSourceCountry());
        FeeSetResponse.Country destinationCountry = feeMapper.toFeeSetCountryResponse(feeParameter.getSourceDestinationCountry()
                .getDestinationCountry());

        feeSetResponse.setCurrency(feeParameter.getCurrency().getCode());
        feeSetResponse.setPayoutMethod(feeParameter.getPayoutMethod().name());
        feeSetResponse.setPaymentMethod(feeParameter.getPaymentMethod().name());
        feeSetResponse.setSourceCountry(sourceCountry);
        feeSetResponse.setDestinationCountry(destinationCountry);

        return feeSetResponse;
    }

    private List<FeeRangeResponse> getFeeRangeResponseList(FeeParameter feeParameter) {
        List<FeeRange> feeRanges = feeRangeRepository.findAllByFeeParameterAndActive(feeParameter, true);

        return feeRanges.stream().map(feeRange -> getFeeRangeResponse(feeRange)).collect(Collectors.toList());
    }

    private FeeRangeResponse getFeeRangeResponse(FeeRange feeRange) {
        FeeRangeResponse feeRangeResponse = new FeeRangeResponse();
        feeRangeResponse.setFlatFee(feeRange.getFlatFee());
        feeRangeResponse.setPercentageFee(feeRange.getPercentageFee());
        feeRangeResponse.setMaxAmount(feeRange.getMaxAmount());
        feeRangeResponse.setMinAmount(feeRange.getMinAmount());

        return feeRangeResponse;
    }

    public List<FeeSetResponse> getFeeSetsBySourceDestinationCountry(String sourceCountryCode,
                                                                     String destinationCountryCode) {
        List<FeeParameter> feeParameters =
                feeParameterRepository.findAllBySourceDestinationCountry(getSourceDestinationCountry(sourceCountryCode,
                        destinationCountryCode));

        return feeParameters.stream().map(feeParameter -> {
            FeeSetResponse feeSetResponse = getFeeSetResponse(feeParameter);
            feeSetResponse.setFeeRanges(getFeeRangeResponseList(feeParameter));

            return feeSetResponse;
        }).collect(Collectors.toList());
    }

    private SourceDestinationCountry getSourceDestinationCountry(String sourceCountryCode,
                                                                 String destinationCountryCode) {
        Country sourceCountry = countryService.findByThreeCharCode(sourceCountryCode);
        Country destinationCountry = countryService.findByThreeCharCode(destinationCountryCode);
        return sourceDestinationCountryService.findBySourceCountryAndDestinationCountry(sourceCountry,
                destinationCountry);
    }

    private BigDecimal calculateTotalFee(FeeRange feeRange, BigDecimal senderAmount) {
        BigDecimal percentageFee =
                feeRange.getPercentageFee()
                        .divide(BigDecimal.valueOf(100))
                        .multiply(senderAmount)
                        .setScale(2, RoundingMode.HALF_UP);

        return percentageFee.add(feeRange.getFlatFee());
    }
}
