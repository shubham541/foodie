package com.bits.foodie.repository;

import com.bits.foodie.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface AdminRepository extends JpaRepository<Admin,Integer> {
    Optional<Admin> findOneByAdminEmailAndPassword(String adminEmail, String password);
    Admin findByAdminEmail(String adminEmail);
}
