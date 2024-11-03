package com.bits.foodie.repository;

import com.bits.foodie.model.DeliveryPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson,Integer> {
    Optional<DeliveryPerson> findOneByDeliveryPersonEmailAndPassword(String deliveryPersonEmail, String password);
    DeliveryPerson findByDeliveryPersonEmail(String adminEmail);

    DeliveryPerson findByDeliveryPersonId(int deliveryPersonId);
}
