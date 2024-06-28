package com.machpay.affiliate.user.admin;

import com.machpay.affiliate.entity.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface AdminMapper {
    @Mapping(target = "roles", ignore = true)
    AdminResponse toAdminResponse(Admin admin);
}
