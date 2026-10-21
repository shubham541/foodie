package com.bits.foodie.security;

import com.bits.foodie.config.AppProperties;
import com.bits.foodie.domain.Role;
import com.bits.foodie.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final Duration expiry;

    public JwtService(AppProperties props) {
        String secret = props.getJwt().getSecret();
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("foodie.jwt.secret must be at least 32 characters (set FOODIE_JWT_SECRET).");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiry = Duration.ofMinutes(props.getJwt().getExpiryMinutes());
    }

    public long expirySeconds() {
        return expiry.toSeconds();
    }

    public String issue(User user) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiry.toMillis()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Verifies signature and expiry. Throws {@link JwtException} (or IllegalArgumentException) when invalid. */
    public AuthUser parse(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return new AuthUser(Long.valueOf(claims.getSubject()), claims.get("email", String.class),
                Role.valueOf(claims.get("role", String.class)));
    }
}
