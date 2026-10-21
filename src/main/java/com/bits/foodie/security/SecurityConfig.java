package com.bits.foodie.security;

import com.bits.foodie.exception.ApiExceptionHandler.ErrorResponse;
import com.bits.foodie.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import java.io.IOException;
import java.time.Instant;

/**
 * Stateless JWT security. CSRF protection is off on purpose: authentication uses an Authorization header
 * (never cookies), so a forged cross-site request cannot carry credentials.
 * Fine-grained rules (ownership, state transitions) live in the services.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService, UserRepository users,
                                            ObjectMapper mapper) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(h -> h
                        .frameOptions(f -> f.deny())
                        .referrerPolicy(r -> r.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)))
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/api/v1/auth/login", "/api/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/restaurants", "/api/v1/restaurants/**").permitAll()
                        .requestMatchers("/api/v1/restaurant-admin/**").hasRole("RESTAURANT")
                        .requestMatchers("/api/v1/customers/**").hasRole("CUSTOMER")
                        .requestMatchers("/api/**").authenticated()
                        // pages, static assets, swagger and health are public; pages enforce roles via the API they call
                        .anyRequest().permitAll())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> writeError(mapper, res, req.getRequestURI(), HttpStatus.UNAUTHORIZED,
                                "Please log in to continue."))
                        .accessDeniedHandler((req, res, ex) -> writeError(mapper, res, req.getRequestURI(), HttpStatus.FORBIDDEN,
                                "You do not have permission to do that.")))
                .addFilterBefore(new JwtAuthFilter(jwtService, users), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private static void writeError(ObjectMapper mapper, HttpServletResponse res, String path, HttpStatus status, String message)
            throws IOException {
        res.setStatus(status.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(res.getOutputStream(),
                new ErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(), message, path, null));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
