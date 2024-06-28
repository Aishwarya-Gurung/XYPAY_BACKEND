package com.machpay.affiliate.country.sourcedestinationcountry;

import com.machpay.affiliate.entity.Country;
import com.machpay.affiliate.entity.SourceDestinationCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SourceDestinationCountryRepository extends JpaRepository<SourceDestinationCountry, Long> {

    boolean existsBySourceCountry(Country country);

    boolean existsByDestinationCountry(Country country);

    List<SourceDestinationCountry> getAllBySourceCountry(Country country);

    SourceDestinationCountry findBySourceCountryAndDestinationCountry(Country sourceCountry,
                                                                      Country destinationCountry);

    List<SourceDestinationCountry> findAllBySourceCountry(Country sourceCountry);

    @Query("SELECT DISTINCT sourceCountry FROM SourceDestinationCountry")
    List<Country> findDistinctSourceCountry();
}
