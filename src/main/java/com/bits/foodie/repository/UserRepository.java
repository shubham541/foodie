package com.bits.foodie.repository;

import com.bits.foodie.domain.Role;
import com.bits.foodie.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<User> findByRoleOrderByIdAsc(Role role);

    long countByRoleAndActiveTrue(Role role);
}
