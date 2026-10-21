package com.bits.foodie.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/** In the "prod" profile, refuse to start with the development defaults for secrets and seeded passwords. */
@Component
public class ProductionGuard {

    private final AppProperties props;

    public ProductionGuard(AppProperties props) {
        this.props = props;
    }

    @PostConstruct
    void check() {
        if (!props.isProduction()) {
            return;
        }
        if (props.getJwt().getSecret() == null || props.getJwt().getSecret().startsWith("dev-only")) {
            throw new IllegalStateException("Production: set FOODIE_JWT_SECRET to a strong random value (32+ characters).");
        }
        AppProperties.Seed s = props.getSeed();
        if (s.isEnabled() && isDefault(s.getRestaurantPassword(), "Shukita@12345")) {
            throw new IllegalStateException("Production: set FOODIE_RESTAURANT_PASSWORD "
                    + "(or an empty value to skip that account) instead of the development default.");
        }
    }

    private static boolean isDefault(String actual, String devDefault) {
        return devDefault.equals(actual);
    }
}
