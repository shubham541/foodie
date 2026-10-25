package com.bits.foodie;

import com.bits.foodie.config.AppProperties;
import com.bits.foodie.config.ProductionGuard;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductionGuardTest {

    private static AppProperties props(String jwt, String ownerPassword) {
        AppProperties p = new AppProperties();
        p.setProduction(true);
        p.getJwt().setSecret(jwt);
        p.getSeed().setEnabled(true);
        p.getSeed().setRestaurantPassword(ownerPassword);
        return p;
    }

    private static final String STRONG = "a-long-random-jwt-secret-for-tests-0123456789";

    @Test
    void acceptsAHostedDatabaseWithRealSecrets() {
        assertThatCode(() -> new ProductionGuard(props(STRONG, "Real-Pass-1"), "jdbc:mysql://db:3306/x", "s3cret-db").check())
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsLocalDatabaseAndDefaultSecrets() {
        assertThatThrownBy(() -> new ProductionGuard(props(STRONG, "Real-Pass-1"), "jdbc:mysql://localhost:3306/x", "s3cret-db").check())
                .hasMessageContaining("localhost");
        assertThatThrownBy(() -> new ProductionGuard(props(STRONG, "Real-Pass-1"), "jdbc:mysql://db:3306/x", "password").check())
                .hasMessageContaining("FOODIE_DB_PASSWORD");
        assertThatThrownBy(() -> new ProductionGuard(props(STRONG, "Shukita@12345"), "jdbc:mysql://db:3306/x", "s3cret-db").check())
                .hasMessageContaining("FOODIE_RESTAURANT_PASSWORD");
        assertThatThrownBy(() -> new ProductionGuard(props("dev-only-secret", "Real-Pass-1"), "jdbc:mysql://db:3306/x", "s3cret-db").check())
                .hasMessageContaining("FOODIE_JWT_SECRET");
    }
}
