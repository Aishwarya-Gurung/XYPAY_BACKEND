package com.machpay.affiliate.device;

import com.machpay.affiliate.device.dto.DeviceResponse;
import com.machpay.affiliate.entity.Device;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface DeviceMapper {

    List<DeviceResponse> toDeviceResponse(List<Device> devices);
}
