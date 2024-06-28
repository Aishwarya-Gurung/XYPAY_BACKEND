package com.machpay.affiliate.common.enums.converter;

import com.machpay.affiliate.common.enums.DeviceType;
import org.springframework.core.convert.converter.Converter;

public class StringToDeviceTypeConverter implements Converter<String, DeviceType> {
    @Override
    public DeviceType convert(String source) {
        try {
            return DeviceType.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
