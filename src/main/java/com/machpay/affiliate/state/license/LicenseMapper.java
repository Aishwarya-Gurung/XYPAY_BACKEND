package com.machpay.affiliate.state.license;

import com.machpay.affiliate.entity.License;
import com.machpay.affiliate.state.license.dto.LicenseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface LicenseMapper {
    LicenseResponse toLicenseResponse(License license);

    List<LicenseResponse> toLicenseResponse(List<License> license);
}
