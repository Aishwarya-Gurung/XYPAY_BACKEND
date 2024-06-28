package com.machpay.affiliate.country.sourcedestinationcountry;

import com.machpay.affiliate.entity.Country;
import com.machpay.affiliate.entity.SourceDestinationCountry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SourceDestinationCountryService {
    @Autowired
    private SourceDestinationCountryRepository sourceDestinationCountryRepository;

    public SourceDestinationCountry save(SourceDestinationCountry sourceDestinationCountry) {
        return sourceDestinationCountryRepository.save(sourceDestinationCountry);
    }

    public SourceDestinationCountry findBySourceCountryAndDestinationCountry(Country sourceCountry,
                                                                             Country destinationCountry) {
        return sourceDestinationCountryRepository.findBySourceCountryAndDestinationCountry(sourceCountry,
                destinationCountry);
    }
}
