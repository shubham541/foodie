package com.bits.foodie.service;

import com.bits.foodie.domain.Role;
import com.bits.foodie.domain.User;
import com.bits.foodie.dto.AuthDtos.*;
import com.bits.foodie.exception.ApiException;
import com.bits.foodie.repository.UserRepository;
import com.bits.foodie.security.JwtService;
import com.bits.foodie.security.LoginAttemptService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    /** Same message for unknown email and wrong password so the API does not reveal which emails exist. */
    private static final String BAD_CREDENTIALS = "Invalid email or password.";

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final LoginAttemptService attempts;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt, LoginAttemptService attempts) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
        this.attempts = attempts;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) {
            throw ApiException.conflict("An account with this email already exists.");
        }
        User u = new User();
        u.setName(req.name().trim());
        u.setEmail(email);
        u.setPhone(req.phone());
        u.setPasswordHash(encoder.encode(req.password()));
        u.setRole(Role.CUSTOMER);
        users.save(u);
        return response(u);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase();
        attempts.checkAllowed(email);
        User u = users.findByEmailIgnoreCase(email).orElse(null);
        if (u == null || !encoder.matches(req.password(), u.getPasswordHash())) {
            attempts.recordFailure(email);
            throw ApiException.unauthorized(BAD_CREDENTIALS);
        }
        if (!u.isActive()) {
            throw ApiException.forbidden("Your account has been deactivated. Please contact support.");
        }
        attempts.recordSuccess(email);
        return response(u);
    }

    @Transactional(readOnly = true)
    public UserDTO me(Long userId) {
        return DtoMapper.user(users.findById(userId).orElseThrow(() -> ApiException.unauthorized("Please log in again.")));
    }

    private AuthResponse response(User u) {
        return new AuthResponse(jwt.issue(u), "Bearer", jwt.expirySeconds(), DtoMapper.user(u));
    }
}
