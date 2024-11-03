package com.bits.foodie.repository;

import com.bits.foodie.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    Optional<Customer> findOneByCustomerEmailAndPassword(String customerEmail, String password);
    Customer findByCustomerEmail(String customerEmail);
    Customer findByCustomerId(int customerId);

    int countByIsActive(boolean b);
}
