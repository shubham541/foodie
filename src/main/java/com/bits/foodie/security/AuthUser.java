package com.bits.foodie.security;

import com.bits.foodie.domain.Role;

/** The authenticated caller, as stored in the Spring Security context. */
public record AuthUser(Long id, String email, Role role) {
}
