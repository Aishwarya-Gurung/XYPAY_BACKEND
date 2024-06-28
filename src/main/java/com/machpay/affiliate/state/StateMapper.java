package com.machpay.affiliate.state;

import com.machpay.affiliate.entity.State;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface StateMapper {

    StateResponse toStateResponse(State state);

    @IterableMapping(qualifiedByName = "toStateResponseList")
    List<StateResponse> toStateResponseList(List<State> states);
}
