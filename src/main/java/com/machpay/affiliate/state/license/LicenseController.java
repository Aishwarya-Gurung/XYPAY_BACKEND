package com.machpay.affiliate.state.license;

import com.machpay.affiliate.common.ListResponse;
import com.machpay.affiliate.state.license.dto.LicenseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/v1/license")
public class LicenseController {

    @Autowired
    private LicenseService licenseService;

    @GetMapping("")
    public ListResponse getAllLicense() {
        List<LicenseResponse> licenseResponse = licenseService.getLicenseList();

        return new ListResponse(licenseResponse);
    }

    @GetMapping("/{stateCode}")
    public LicenseResponse getLicenseByState(@PathVariable("stateCode") @NotBlank String stateCode) {
        return licenseService.getLicenseByState(stateCode);
    }
}
