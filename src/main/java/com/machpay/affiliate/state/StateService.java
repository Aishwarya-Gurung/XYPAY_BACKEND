package com.machpay.affiliate.state;

import com.machpay.affiliate.entity.Country;
import com.machpay.affiliate.entity.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StateService {

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private StateMapper stateMapper;

    public State findByCode(String code) {
        return stateRepository.findByCode(code);
    }

    public Boolean isStateAvailable(String code) {
        return stateRepository.existsByCode(code);
    }

    public List<StateResponse> getStateList() {

        List<State> states = stateRepository.findAll();

        return stateMapper.toStateResponseList(states);
    }

    public List<StateResponse> getStateListByCountry(Country country) {
        List<State> states = stateRepository.findAllByCountryAndActiveTrue(country);

        return stateMapper.toStateResponseList(states);
    }

}
