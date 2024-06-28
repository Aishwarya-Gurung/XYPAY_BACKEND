package com.machpay.affiliate.state;

import com.machpay.affiliate.entity.Country;
import com.machpay.affiliate.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {

    State findByCode(String code);

    Boolean existsByCode(String code);

    List<State> findAllByCountryAndActiveTrue(Country country);
}

