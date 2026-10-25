package com.bits.foodie.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** In the "prod" profile, refuse to start with the development defaults for secrets and seeded passwords. */
@Component
public class ProductionGuard {

    private final AppProperties props;

    private final String dbUrl;
    private final String dbPassword;

    public ProductionGuard(AppProperties props, @Value("${spring.datasource.url:}") String dbUrl,
                           @Value("${spring.datasource.password:}") String dbPassword) {
        this.props = props;
        this.dbUrl = dbUrl;
        this.dbPassword = dbPassword;
    }

    @PostConstruct
    public void check() {
        if (!props.isProduction()) {
            return;
        }
        if (props.getJwt().getSecret() == null || props.getJwt().getSecret().startsWith("dev-only")) {
            throw new IllegalStateException("Production: set FOODIE_JWT_SECRET to a strong random value (32+ characters).");
        }
        if (dbUrl.contains("localhost") || dbUrl.contains("127.0.0.1")) {
            throw new IllegalStateException("Production: FOODIE_DB_URL points at localhost. Use the hosted database.");
        }
        if (isDefault(dbPassword, "password") || isDefault(dbPassword, "change-me")) {
            throw new IllegalStateException("Production: set FOODIE_DB_PASSWORD to a real password.");
        }
        AppProperties.Seed s = props.getSeed();
        if (s.isEnabled() && (isDefault(s.getRestaurantPassword(), "Shukita@12345") || isDefault(s.getRestaurantPassword(), "change-me"))) {
            throw new IllegalStateException("Production: set FOODIE_RESTAURANT_PASSWORD "
                    + "(or an empty value to skip that account) instead of the development default.");
        }
    }

    private static boolean isDefault(String actual, String devDefault) {
        return devDefault.equals(actual);
    }
}
