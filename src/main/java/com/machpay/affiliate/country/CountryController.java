package com.machpay.affiliate.country;

import com.machpay.affiliate.common.ListResponse;
import com.machpay.affiliate.country.dto.CountryResponse;
import com.machpay.affiliate.entity.Country;
import com.machpay.affiliate.state.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/countries")
public class CountryController {

    @Autowired
    private CountryService countryService;

    @Autowired
    private StateService stateService;

    @GetMapping("/{sourceCountry}/destinations")
    public ListResponse getDestinationCountry(@PathVariable("sourceCountry") String sourceCountry) {
        Country country = countryService.findByThreeCharCode(sourceCountry);
        List<CountryResponse> countryResponses = countryService.getAllDestinationCountryList(country);

        String defaultEncoding = System.getProperty("file.encoding");
        System.out.println("Default Encoding heaheheheh: " + defaultEncoding);

        return new ListResponse(countryResponses);
    }

    @GetMapping("")
    public ListResponse getSourceCountry() {
        List<CountryResponse> countryResponses = countryService.getAllSourceCountryList();

        return new ListResponse(countryResponses);
    }

    @GetMapping("/corridors")
    public ListResponse getAllSourceDestinationCountries() {
        return new ListResponse(countryService.getAllSourceDestinationCountries());
    }
}
