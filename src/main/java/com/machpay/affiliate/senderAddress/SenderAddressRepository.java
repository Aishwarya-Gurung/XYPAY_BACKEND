package com.machpay.affiliate.senderAddress;

import com.machpay.affiliate.entity.Sender;
import com.machpay.affiliate.entity.SenderAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SenderAddressRepository extends JpaRepository<SenderAddress, Long> {

    boolean existsBySender(Sender sender);
    
    SenderAddress findBySender(Sender sender);

    boolean existsSenderAddressBySenderAndCityNotNull(Sender Sender);
}
