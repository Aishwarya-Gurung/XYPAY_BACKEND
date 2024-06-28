package com.machpay.affiliate.state.license;

import com.machpay.affiliate.common.exception.ResourceNotFoundException;
import com.machpay.affiliate.entity.License;
import com.machpay.affiliate.entity.State;
import com.machpay.affiliate.state.StateService;
import com.machpay.affiliate.state.license.dto.LicenseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LicenseService {

    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private LicenseMapper licenseMapper;

    @Autowired
    private StateService stateService;

    public List<LicenseResponse> getLicenseList() {
        List<License> license = licenseRepository.findAll();

        return licenseMapper.toLicenseResponse(license);
    }

    public LicenseResponse getLicenseByState(String stateCode) {
        State state = stateService.findByCode(stateCode);
        License license = licenseRepository.getLicenseByState(state)
                .orElseThrow(() -> new ResourceNotFoundException("License", "stateCode", stateCode));
        LicenseResponse licenseResponse = licenseMapper.toLicenseResponse(license);
        licenseResponse.setMsb(state.getMsb());
        return licenseResponse ;
    }
}
