package com.bits.foodie.security;

import com.bits.foodie.domain.User;
import com.bits.foodie.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Reads "Authorization: Bearer ..." and populates the security context. The role and active flag are
 * re-read from the database on every request, so deactivating a user or changing a role takes effect immediately.
 * Deliberately not a Spring bean: it is added to the security filter chain only (a bean would also be registered as a servlet filter).
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);
    private static final String PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserRepository users;

    public JwtAuthFilter(JwtService jwtService, UserRepository users) {
        this.jwtService = jwtService;
        this.users = users;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith(PREFIX) && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                AuthUser claims = jwtService.parse(header.substring(PREFIX.length()).trim());
                User user = users.findById(claims.id()).orElse(null);
                if (user != null && user.isActive()) {
                    AuthUser principal = new AuthUser(user.getId(), user.getEmail(), user.getRole());
                    var auth = new UsernamePasswordAuthenticationToken(principal, null,
                            List.of(new SimpleGrantedAuthority(user.getRole().authority())));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (JwtException | IllegalArgumentException e) {
                log.debug("Rejected bearer token: {}", e.getMessage());
            }
        }
        chain.doFilter(request, response);
    }
}
