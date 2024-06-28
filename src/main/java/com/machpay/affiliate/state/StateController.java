package com.machpay.affiliate.state;

import com.machpay.affiliate.common.ListResponse;
import com.machpay.affiliate.country.CountryService;
import com.machpay.affiliate.entity.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class StateController {

    @Autowired
    private StateService stateService;

    @Autowired
    private CountryService countryService;

    @GetMapping("/states")
    public ResponseEntity getAllStates() {
        List<StateResponse> stateResponses = stateService.getStateList();

        return ResponseEntity.ok(new ListResponse(stateResponses));
    }

    @GetMapping("/states/{sourceCountry}")
    public ResponseEntity getStatesByCountry(@PathVariable("sourceCountry") @NotBlank String sourceCountry) {
        Country country = countryService.findByThreeCharCode(sourceCountry);
        List<StateResponse> stateResponses = stateService.getStateListByCountry(country);

        return ResponseEntity.ok(new ListResponse(stateResponses));
    }
}
