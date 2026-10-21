package com.bits.foodie;

import com.bits.foodie.config.AppProperties;
import com.bits.foodie.domain.Role;
import com.bits.foodie.domain.User;
import com.bits.foodie.security.AuthUser;
import com.bits.foodie.security.JwtService;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static JwtService service(String secret, long minutes) {
        AppProperties props = new AppProperties();
        props.getJwt().setSecret(secret);
        props.getJwt().setExpiryMinutes(minutes);
        return new JwtService(props);
    }

    private static User user() {
        User u = new User();
        u.setId(42L);
        u.setEmail("a@b.com");
        u.setRole(Role.RESTAURANT);
        return u;
    }

    private static final String SECRET = "unit-test-secret-unit-test-secret-0123456789";

    @Test
    void roundTripsIdEmailAndRole() {
        JwtService jwt = service(SECRET, 5);
        AuthUser parsed = jwt.parse(jwt.issue(user()));
        assertThat(parsed).isEqualTo(new AuthUser(42L, "a@b.com", Role.RESTAURANT));
    }

    @Test
    void rejectsTamperedTokens() {
        JwtService jwt = service(SECRET, 5);
        String token = jwt.issue(user());
        String tampered = token.substring(0, token.length() - 2) + (token.endsWith("AA") ? "BB" : "AA");
        assertThatThrownBy(() -> jwt.parse(tampered)).isInstanceOf(JwtException.class);
    }

    @Test
    void rejectsTokensSignedWithAnotherKey() {
        String token = service("another-secret-another-secret-0123456789ab", 5).issue(user());
        assertThatThrownBy(() -> service(SECRET, 5).parse(token)).isInstanceOf(JwtException.class);
    }

    @Test
    void rejectsExpiredTokens() {
        JwtService jwt = service(SECRET, -1);
        assertThatThrownBy(() -> jwt.parse(jwt.issue(user()))).isInstanceOf(JwtException.class);
    }

    @Test
    void refusesWeakSecrets() {
        assertThatThrownBy(() -> service("too-short", 5)).isInstanceOf(IllegalStateException.class);
    }
}
