package com.bits.foodie.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Typed view of the {@code foodie.*} settings in application.properties. */
@Getter
@Setter
@ConfigurationProperties(prefix = "foodie")
public class AppProperties {

    private boolean production;
    private final Jwt jwt = new Jwt();
    private final Order order = new Order();
    private final Seed seed = new Seed();
    private final Security security = new Security();

    @Getter
    @Setter
    public static class Jwt {
        private String secret;
        private long expiryMinutes = 480;
    }

    @Getter
    @Setter
    public static class Order {
        private int maxQuantityPerItem = 20;
    }

    @Getter
    @Setter
    public static class Seed {
        private boolean enabled = true;
        private String restaurantEmail;
        private String restaurantPassword;
    }

    @Getter
    @Setter
    public static class Security {
        private int maxFailedLogins = 5;
        private int lockoutMinutes = 15;
    }
}
