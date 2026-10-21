package com.bits.foodie.config;

import com.bits.foodie.domain.MenuItem;
import com.bits.foodie.domain.Restaurant;
import com.bits.foodie.domain.Role;
import com.bits.foodie.domain.User;
import com.bits.foodie.repository.MenuItemRepository;
import com.bits.foodie.repository.RestaurantRepository;
import com.bits.foodie.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.RoundingMode;
import java.util.List;

/**
 * Idempotent start-up seeding of the House of Shukita restaurant, its menu (seed/house-of-shukita.json)
 * and the restaurant-owner account (the owner manages everything). Safe to run on every boot: existing rows
 * (including prices or availability changed by the owner) are never overwritten; only missing dishes are added
 * and dishes that still have no photo get the one from the seed file.
 */
@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private static final String IMAGE_BASE = "/images/menu/";

    private final AppProperties props;
    private final UserRepository users;
    private final RestaurantRepository restaurants;
    private final MenuItemRepository menuItems;
    private final PasswordEncoder encoder;
    private final ObjectMapper mapper;

    public DataSeeder(AppProperties props, UserRepository users, RestaurantRepository restaurants,
                      MenuItemRepository menuItems, PasswordEncoder encoder, ObjectMapper mapper) {
        this.props = props;
        this.users = users;
        this.restaurants = restaurants;
        this.menuItems = menuItems;
        this.encoder = encoder;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (!props.getSeed().isEnabled()) {
            return;
        }
        AppProperties.Seed s = props.getSeed();
        User owner = ensureOwner(s.getRestaurantEmail(), s.getRestaurantPassword());
        if (owner != null) {
            seedRestaurant(owner);
        }
    }

    /**
     * The single restaurant owner. If the configured email changed since the account was created, the existing
     * owner is renamed instead of creating a second owner (and a second restaurant).
     */
    private User ensureOwner(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return null;
        }
        String wanted = email.trim().toLowerCase();
        User byEmail = users.findByEmailIgnoreCase(wanted).orElse(null);
        if (byEmail != null) {
            return byEmail;
        }
        List<User> owners = users.findByRoleOrderByIdAsc(Role.RESTAURANT);
        if (owners.size() == 1) {
            owners.get(0).setEmail(wanted);
            log.info("Restaurant owner login changed to {}", wanted);
            return owners.get(0);
        }
        return ensureUser("House of Shukita", email, password, Role.RESTAURANT, "9000000001");
    }

    private User ensureUser(String name, String email, String password, Role role, String phone) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return null;
        }
        return users.findByEmailIgnoreCase(email.trim()).orElseGet(() -> {
            User u = new User();
            u.setName(name);
            u.setEmail(email.trim().toLowerCase());
            u.setPhone(phone);
            u.setPasswordHash(encoder.encode(password));
            u.setRole(role);
            log.info("Seeded {} account {}", role, u.getEmail());
            return users.save(u);
        });
    }

    private void seedRestaurant(User owner) throws Exception {
        JsonNode root;
        try (InputStream in = new ClassPathResource("seed/house-of-shukita.json").getInputStream()) {
            root = mapper.readTree(in);
        }
        JsonNode r = root.get("restaurant");
        Restaurant restaurant = restaurants.findByOwnerId(owner.getId()).orElseGet(() -> {
            Restaurant created = new Restaurant();
            created.setOwner(owner);
            created.setName(r.get("name").asText());
            created.setDescription(text(r, "description"));
            created.setCuisine(text(r, "cuisine"));
            created.setAddress(text(r, "address"));
            created.setLogoUrl(text(r, "logoUrl"));
            created.setHeroUrl(text(r, "heroUrl"));
            created.setTaxRatePercent(r.get("taxRatePercent").decimalValue());
            log.info("Seeded restaurant {}", created.getName());
            return restaurants.save(created);
        });

        List<MenuItem> existing = menuItems.findByRestaurantIdOrderBySortOrderAscIdAsc(restaurant.getId());
        int order = 0;
        int added = 0;
        for (JsonNode n : root.get("items")) {
            order++;
            String name = n.get("name").asText();
            MenuItem known = existing.stream().filter(m -> m.getName().equals(name)).findFirst().orElse(null);
            if (known != null) {
                if (known.getImageUrl() == null && n.hasNonNull("image")) {
                    known.setImageUrl(IMAGE_BASE + n.get("image").asText()); // photos added after the first seed
                }
                continue;
            }
            MenuItem m = new MenuItem();
            m.setRestaurant(restaurant);
            m.setName(name);
            m.setCategory(n.get("category").asText());
            m.setDescription(text(n, "description"));
            m.setPrice(n.get("price").decimalValue().setScale(2, RoundingMode.HALF_UP));
            m.setVeg(n.hasNonNull("veg") ? n.get("veg").asBoolean() : null);
            m.setImageUrl(n.hasNonNull("image") ? IMAGE_BASE + n.get("image").asText() : null);
            m.setSortOrder(order);
            menuItems.save(m);
            added++;
        }
        if (added > 0) {
            log.info("Seeded {} menu item(s) for {}", added, restaurant.getName());
        }
    }

    private static String text(JsonNode n, String field) {
        return n.hasNonNull(field) ? n.get(field).asText() : null;
    }

}
