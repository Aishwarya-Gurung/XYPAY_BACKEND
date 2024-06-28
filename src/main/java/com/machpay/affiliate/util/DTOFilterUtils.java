package com.machpay.affiliate.util;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.machpay.affiliate.common.enums.DTOFilters;
import com.machpay.affiliate.user.sender.dto.SenderAuthResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.converter.json.MappingJacksonValue;

import java.util.Set;

@UtilityClass
public class DTOFilterUtils {

    public static MappingJacksonValue filterSenderAuthResponse(SenderAuthResponse senderAuthResponse, DTOFilters dtoFilter, Set<String> ignoreFields) {
        String filterName = dtoFilter.getValue();
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(ignoreFields);
        FilterProvider filterProvider = new SimpleFilterProvider().addFilter(filterName, filter);
        MappingJacksonValue filteredResponse = new MappingJacksonValue(senderAuthResponse);
        filteredResponse.setFilters(filterProvider);

        return filteredResponse;
    }
}
